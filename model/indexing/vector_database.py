import numpy as np
import faiss
import os


data_path = 'path/to/database/indices'

# Load the embeddings
sketch_embeddings: np.ndarray = np.load(os.path.join(data_path, 'data', 'sketch_embeddings_7.npy'))
assert sketch_embeddings.shape == (75481, 4608)

photo_embeddings: np.ndarray = np.load(os.path.join(data_path, 'data', 'photo_embeddings_7.npy'))
assert photo_embeddings.shape == (12500, 4608)

# Data paths
sketches_path = 'path/to/database/sketches'

photos_path = 'path/to/database/images'

classes = os.listdir(sketches_path)
assert len(classes) == 125

# This contains all associations between classes and their indices
# The tuple contains sketch vectors index and the photos vectors index
index: dict[str, tuple[faiss.IndexIVFFlat, faiss.IndexIVFFlat]] = dict()



print("Indexing sketches and photos...")
sketch_index = 0
photo_index = 0
for c in classes:

    sketch_class_path = os.path.join(sketches_path, c)
    photo_class_path = os.path.join(photos_path, c)

    num_sketches = len(os.listdir(sketch_class_path))
    num_photos = len(os.listdir(photo_class_path))

    class_sketches = sketch_embeddings[sketch_index: sketch_index + num_sketches]
    class_photos = photo_embeddings[photo_index: photo_index + num_photos]

    # Create indices
    d = 4608

    sketch_quantizer = faiss.IndexFlatIP(d)
    # sketch_db = faiss.IndexIVFFlat(sketch_quantizer, d, 5, faiss.METRIC_L2)
    # sketch_db.train(class_sketches)
    # sketch_db.add(class_sketches)
    sketch_quantizer.add(class_sketches)


    photo_quantizer = faiss.IndexFlatIP(d)
    # photo_db = faiss.IndexIVFFlat(photo_quantizer, d, 1, faiss.METRIC_L2)
    # photo_db.train(class_photos)
    # photo_db.add(class_photos)
    photo_quantizer.add(class_photos)

    index[c] = (sketch_quantizer, photo_quantizer)#(sketch_db, photo_db)

    sketch_index += num_sketches
    photo_index += num_photos

print("Indexing sketches & photos done")


def get_closest_sketch_and_n_photos(class_name: str, n: int, embedding: np.ndarray):
    """
    Finds the best matching sketch and n matching photos

    :param class_name: the class to search in (eg. airplane, apple, etc..)
    :param n: max number of photos to retrieve
    :param embedding: the feature vector of the query
    :return: tuple of a sketch index and a list of n photo indices. tuple(int, list[int]))
    """
    (sketch_db, photo_db) = index[class_name]

    # Search the sketchIndex and photosIndex
    _, CI = sketch_db.search(np.array([embedding]), 1)
    _, PI = photo_db.search(np.array([embedding]), n)

    return CI[0][0], PI[0]


