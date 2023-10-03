import cv2
import os
import numpy as np
import matplotlib.pyplot as plt
from keras.applications.vgg16 import preprocess_input
import model_inference as m

photos_dir = 'path/to/database/images'
classes = os.listdir(photos_dir)


def get_photo_embedding(path: str, class_index=0):
    img: np.ndarray = cv2.imread(path)
    img = cv2.resize(img, (224, 224))

    # All proposed image edges
    edge_1 = 255 - cv2.Canny(img, 100, 200)
    edge_2 = 255 - cv2.Canny(img, 200, 300)
    edge_3 = 255 - cv2.Canny(img, 60, 100)
    edge_4 = 255 - cv2.Canny(img, 300, 450)
    edge_5 = 255 - cv2.Canny(img, 400, 550)

    edge_rep = (edge_1, edge_2, edge_3, edge_4, edge_5)

    rgb_edges = np.array([
        cv2.cvtColor(x, cv2.COLOR_GRAY2RGB)
        for x in edge_rep
    ])

    # preprocess input according to vgg16 and get the predictions
    processed_input = preprocess_input(rgb_edges)
    predictions = m.prediction_model.predict(processed_input)

    # Find the edge map which produced the best prediction on our class
    best_edge = processed_input[np.argmax(predictions[:, class_index])]

    # extract its feature vectors
    features = m.features_model.predict(np.array([best_edge]))
    return features[0]


photos_features = list()
i = 0
for (directory, _, files) in os.walk(photos_dir):
    if len(files) == 0:
        continue

    class_name = classes[i]

    for f in files:
        embeddings = get_photo_embedding(os.path.join(directory, f), class_index=i)
        photos_features.append(embeddings)

    i += 1

# save photos features
photos_features = np.array(photos_features)
print("Final photos embeddings: ", photos_features.shape)
np.save('./data/photo_embeddings_7', photos_features)
