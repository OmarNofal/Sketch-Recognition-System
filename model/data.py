from keras.utils import image_dataset_from_directory
import random
from keras.applications.vgg16 import preprocess_input

data_path = '/path/to/dataset'


def get_dataset():

    t_dataset, v_dataset = image_dataset_from_directory(
        data_path,
        color_mode='rgb',
        batch_size=64,
        validation_split=0.20,
        subset='both',
        label_mode='categorical',
        seed=int(random.random()*255),
        image_size=(224, 224)
    )

    return t_dataset.map(lambda x1, y1: (preprocess_input(x1), y1)), v_dataset.map(lambda x2, y2: (preprocess_input(x2), y2))
