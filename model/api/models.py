from keras.models import load_model, Model
from keras.layers import Flatten, MaxPool2D
import os


# Disable GPU usage, since the cost of receiving the results
# is much larger than the prediction cost on CPU
os.environ["CUDA_VISIBLE_DEVICES"] = "-1"


model7_prediction: Model = load_model('../model/XNet7')

model7_features = model7_prediction.layers[-3].output
model7_features = Flatten(name='flatten_features')(MaxPool2D(strides=(2, 2), pool_size=(2, 2))(model7_features))
model7_features = Model(inputs=model7_prediction.input, outputs=model7_features)

print(model7_prediction.output)
print(model7_prediction.summary())

model7 = Model(inputs=model7_prediction.input, outputs=[model7_prediction.output, model7_features.output])
print(model7.summary())


model5: Model = load_model('../model/XNet5')
