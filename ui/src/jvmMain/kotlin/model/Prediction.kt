package model



abstract class ClassPrediction(
    val className: String,
    val probability: Float // 0 - 1
)

class Network5ClassPrediction(
    className: String,
    probability: Float,
) : ClassPrediction(className, probability)


class Network7ClassPrediction(
    className: String,
    probability: Float,
    val bestSketchPath: String,
    val bestPhotosPaths: List<String>
) : ClassPrediction(className, probability)