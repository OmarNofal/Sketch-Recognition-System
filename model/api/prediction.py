import sys



sys.path.append('..')

from keras.applications.vgg16 import preprocess_input
from models import model7, model5
from database import photos_collection, sketch_collection
from indexing.vector_database import get_closest_sketch_and_n_photos
from timer import Timer

import numpy as np
import os


sketches_path = 'C:\\Users\\omarw\\OneDrive - student.guc.edu.eg\\8th Semester\\Bachelor\\Implementation\\Concept #4 ' \
                '(Deep Learning)\\data\\data\\256x256\\sketch\\data_centered'
photos_path = 'C:\\Users\\omarw\\OneDrive - student.guc.edu.eg\\8th Semester\\Bachelor\\Implementation\\Concept #4 (' \
              'Deep Learning)\\data\\data\\256x256\\photo\\tx_000000000000'

class_names = os.listdir(sketches_path)


# Note: This function assumes the input is of
# shape (256 * 256 * 1, ) and contains grayscale values
# from 0 - 255. The function reshapes it to (1, 256, 256, 1)
def preprocess_model5(img: bytes):
    array = np.frombuffer(img, dtype=np.uint8).astype(np.float32)
    print(array)
    return array.reshape(1, 256, 256, 1)


# Note: This function assumes the input is of
# shape (224 * 224 * 3, ) and contains rgb values
# from 0 - 255. The function reshapes it to (1, 224, 224, 3)
# and normalizes it for the model
def preprocess_model7(img: bytes):
    array = np.frombuffer(img, dtype=np.uint8).astype(np.float64).reshape(224, 224, 3)
    preprocessed_array = preprocess_input(array)
    return np.array([preprocessed_array])


def predict(img: bytes, version=5):
    if version == 5:
        data = preprocess_model5(img)
        model = model5
        prediction, features = model.predict(data)[0], None
    else:
        data = preprocess_model7(img)
        model = model7

        with Timer("Model prediction"):

            x = model(data)
            prediction = x[0][0].numpy()
            features = x[1][0].numpy()


    largest_indices = np.argsort(prediction)[::-1][:5]

    # if version == 7:
    #     features = model7_features(data)[0]

    result = {}
    for i in range(len(largest_indices)):
        if version == 5:
            index = largest_indices[i]
            result[class_names[index]] = str(round(prediction[index], 2))
            result['version'] = 5

        elif version == 7:
            index = largest_indices[i]
            class_name = class_names[index]
            with Timer("Vector search"):
                sketch_index, photos_indices = get_closest_sketch_and_n_photos(class_names[largest_indices[i]], 5, features)

            with Timer("Database search"):
                sketch_data = sketch_collection.find_one(
                    {'class_relative_index': int(sketch_index), 'class': class_name},
                    projection={'name': True}
                )
                photos_data = photos_collection.find(
                    {
                        'class_relative_index': {'$in': [int(x) for x in photos_indices]},
                        'class': class_name
                    },
                    projection={'name': True})

            result[class_name] = {
                'probability': str(round(prediction[index], 2)),
                'best_sketch_path': os.path.join(sketches_path, class_name, sketch_data['name']),
                'matching_photos_paths': [os.path.join(photos_path, class_name, photo['name']) for photo in photos_data]
            }
            result['version'] = 7

    return result
