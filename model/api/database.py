from pymongo.mongo_client import MongoClient
from pymongo.collection import Collection

connection = MongoClient('localhost', 27017)

db = connection.sketchdb

sketch_collection: Collection = db.sketches
photos_collection: Collection = db.photos
