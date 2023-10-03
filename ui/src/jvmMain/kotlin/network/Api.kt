package network

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.ClassPrediction
import model.Network5ClassPrediction
import model.Network7ClassPrediction
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


enum class NNetVersion(val value: String) {

    /**
     * Custom neural network
     */
    FIVE("5"),

    /**
     * Network using pre-trained VGG16
     */
    SEVEN("7");

}

object Network {

    var version by mutableStateOf(NNetVersion.FIVE)


    private val httpClient = OkHttpClient()


    suspend fun predictTop5(img: ByteArray): List<ClassPrediction> {
        val mediaType = "application/octet-stream".toMediaType()
        val requestBody = img.toRequestBody(mediaType, 0, img.size)
        val request = Request.Builder()
            .url("http://127.0.0.1:5000/predict?version=${version.value}")
            .post(requestBody)
            .build()


        var predictions = listOf<ClassPrediction>()
        withContext(Dispatchers.IO) {
            val responseString = httpClient.newCall(request).execute().body!!.string()
            val json = JsonParser.parseString(responseString).asJsonObject
            val version = json["version"].asInt
            json.remove("version")
            require(version in listOf(5, 7))
            predictions = if (version == 5) {
                parseNetwork5Response(json)
            } else  {
                parseNetwork7Response(json)
            }
        }
        return predictions.sortedByDescending { it.probability }
    }

    private fun parseNetwork5Response(json: JsonObject): List<Network5ClassPrediction> {
        val result = mutableListOf<Network5ClassPrediction>()
        for (p in json.entrySet()) {
            val className = p.key
            val probability = p.value.asString.toFloat()
            result.add(Network5ClassPrediction(className, probability))
        }
        return result
    }

    private fun parseNetwork7Response(json: JsonObject): List<Network7ClassPrediction> {
        val result = mutableListOf<Network7ClassPrediction>()
        for (p in json.entrySet()) {
            val className = p.key
            val data = p.value.asJsonObject
            val probability = data["probability"].asFloat
            val bestSketchPath = data["best_sketch_path"].asString
            val photosPaths = data["matching_photos_paths"].asJsonArray.map { it.asString }
            result.add(Network7ClassPrediction(className, probability, bestSketchPath, photosPaths))
        }
        return result
    }

}