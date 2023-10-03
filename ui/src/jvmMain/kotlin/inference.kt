//import org.deeplearning4j.nn.graph.ComputationGraph
//import org.deeplearning4j.nn.modelimport.keras.KerasModelImport
//import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
//import org.nd4j.linalg.api.buffer.DataType
//import org.nd4j.linalg.api.ndarray.INDArray
//import org.nd4j.linalg.factory.Nd4j
//import java.io.File
//import java.net.HttpURLConnection
//import java.net.URL
//import kotlin.system.measureTimeMillis
//
//
//private const val NUM_CLASSES = 125
//
//private const val MODEL_PATH =
//    "C:\\Users\\omarw\\OneDrive - student.guc.edu.eg\\8th Semester\\Bachelor\\Implementation\\Concept #4 (Deep Learning)\\NetX2\\model\\NetX7_java.h5"
//
//
//private const val CLASSES_FOLDER_PATH =
//    "C:\\Users\\omarw\\OneDrive - student.guc.edu.eg\\8th Semester\\Bachelor\\Implementation\\Concept #4 (Deep Learning)\\data\\data\\256x256\\sketch\\data"
//
//enum class NNetVersion {
//
//    /**
//     * Custom neural network
//     */
//    FIVE,
//
//    /**
//     * Network using pre-trained VGG16
//     */
//    SEVEN
//}
//
//object Model {
//
//    var networkVersion: NNetVersion = NNetVersion.SEVEN
//
//    private lateinit var model: ComputationGraph
//    private lateinit var classes: Array<String>
//
//    private var isLoaded = false
//
//    fun loadModel() {
//        model = KerasModelImport.importKerasModelAndWeights(MODEL_PATH)
//        loadClasses()
//        isLoaded = true
//    }
//
//
//    fun predictTop5(pixels: IntArray): List<Pair<String, Float>> {
//        val modelInput = Nd4j.create(pixels, longArrayOf(1, 224, 224, 3), DataType.INT32)
//
//
//        lateinit var output: Array<INDArray>
//
//        val ms = measureTimeMillis {
//            output = model.output(modelInput)
//        }; println("Function took ${ms}ms")
//
//        return output[0].toFloatVector().zip(classes)
//            .sortedByDescending { it.first }
//            .map { Pair(it.second, it.first) }
//            .take(5)
//    }
//
//    @OptIn(ExperimentalUnsignedTypes::class)
//    fun predictTop5Network(pixles: ByteArray): List<Pair<String, Float>> {
//        val url = URL("http://127.0.0.1:5000/predict")
//        val conn = url.openConnection() as HttpURLConnection
//        conn.requestMethod = "POST"
//        conn.doOutput = true
//        conn.outputStream.write(pixles)
//        conn.outputStream.flush()
//        conn.outputStream.close()
//
//        return listOf()
//    }
//
//    private fun loadClasses() {
//        classes = Array(NUM_CLASSES) { "" }
//        val file = File(CLASSES_FOLDER_PATH)
//        file.listFiles()!!
//            .forEachIndexed { index, classDir ->
//                classes[index] = classDir.name
//            }
//    }
//
//}
//
