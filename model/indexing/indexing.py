import faiss
import numpy as np
from time import perf_counter

# Load the reduced embeddings
vectors: np.ndarray = np.load('data/sketch_embeddings_7.npy')
print(vectors.shape)

# Create index
d = 4608
index = faiss.IndexFlatIP(d)
#index = faiss.IndexIVFFlat(quantizer, d, 100, faiss.METRIC_L2)

# train the index
#index.train(vectors)

# add the data
index.add(vectors)
print(index.is_trained)

index.nprobe = 1
t = perf_counter()
print(index.search(vectors[:10], 50))
print(f"Search took {perf_counter() - t}s")
input()
