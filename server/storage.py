from config import MONGO_CONNECT_URI
from pymongo import MongoClient

db = MongoClient(MONGO_CONNECT_URI).cib2021
