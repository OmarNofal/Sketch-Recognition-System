from keras.models import load_model, Model
from keras.layers import Flatten, MaxPool2D
from keras.utils.image_utils import load_img
import numpy as np
from keras.utils import img_to_array
from keras.applications.vgg16 import preprocess_input


# load model
prediction_model: Model = load_model('../model/XNet7')
x = prediction_model.layers[-3].output
print(x)
features_model = Flatten()(MaxPool2D(strides=(2, 2), pool_size=(2, 2))(x))
features_model = Model(inputs=prediction_model.input, outputs=features_model)
print(features_model.summary())


# infer
def get_sketches_embeddings(images_paths: list[str]):
    images_data = np.array([preprocess_input(img_to_array(load_img(i, target_size=(224, 224), color_mode='rgb'))) for i in images_paths])
    embeddings = features_model.predict(images_data)
    return embeddings
