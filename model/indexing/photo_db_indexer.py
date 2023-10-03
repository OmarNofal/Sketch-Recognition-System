import pymongo
import os

connection = pymongo.mongo_client.MongoClient(host='127.0.0.1', port=27017)

db = connection.sketchdb
collection = db.sketches


photos_dir = 'path/to/centered_sketches'
classes = os.listdir(photos_dir)


i = 0
for (dirname, _, files) in os.walk(photos_dir):
    if len(files) == 0:
        continue
    dirname: str = dirname

    data = [
        {
            'name': f,
            'class': os.path.split(dirname)[-1],
            'absolute_index': i + ri,
            'class_relative_index': ri
        }
        for (ri, f) in enumerate(files)
    ]

    collection.insert_many(data)

    i += len(files)
