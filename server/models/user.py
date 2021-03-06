from storage import db
from functions.generate_token import generate_token
from functions.datetime_isoformat import to_isoformat
from hashlib import sha256
import time

class User:
    def get_json(self):
        parent = self.get_parent()

        return {
            'id': self.id,
            'fullName': self.full_name,
            'login': self.login,
            'parent': {
                'id': parent.id,
                'fullName': parent.full_name,
            } if parent else None,
            'created': to_isoformat(self._id.generation_time)
        }

    def __init__(self, user_object):
        if not user_object:
            raise Exception('User object is empty')

        self._id = user_object['_id']
        self.id = user_object['id']

        self.login = user_object['login']
        self.password = user_object['password']

        self.full_name = user_object['full_name']
        self.parent_id = user_object['parent_id']

        self.score = user_object['score']
        self.action = user_object['action']
        # self.data = user_object['data']

    def __eq__(self, other):
        return self.id == other.id

    # Authorization
    def handle_auth(self, password: str):
        if not sha256(password.encode('utf8')).digest() == self.password:
            raise Exception('Неверный пароль')

        token = generate_token()

        db.sessions.insert_one({
            'user_id': self.id,
            'token': token
        })

        return token

    def drop_action(self):
        self.action = None
        # self.data = {}
        db.users.update_one({
            '_id': self._id
        }, {
            '$set': {
                'action': self.action,
                # 'data': self.data
            }
        })

    # Users chain system
    def get_parent(self):
        if not self.parent_id:
            return None

        parent_object = db.users.find_one({
            'id': self.parent_id
        })

        try:
            return User(parent_object)
        except Exception as e:
            self.drop()

    def get_childs(self):
        return [
            User(user_obj) for user_obj in db.users.find({
                'parent_id': self.id
            })
        ]

    def get_depth(self):
        depth = 0
        parent = self.get_parent()
        while parent:
            depth += 1
            parent = parent.get_parent()

        return depth

    def calculate_score(self):
        tasks = [
            task_obj for task_obj in db.tasks.find({
                'employee_id': self.id,
                'status': 'completed'
            })
        ]
        if tasks:
            return round(
                sum([*[task['difficulty'] for task in tasks], *[0.5]]) / (len(tasks) + 1),
                3
            )
        else:
            return 0.5

    # Remove user
    def drop(self):
        db.users.delete_one({
            'id': self.id
        })
        db.tasks.delete_many({
            'employee_id': self.id
        })
        db.tasks.delete_many({
            'employer_id': self.id
        })
        db.sessions.delete_many({
            'user_id': self.id
        })

    # Class generators
    @classmethod
    def from_id(self, id):
        user_object = db.users.find_one({
            'id': id
        })
        if not user_object:
            raise Exception('Пользователь не найден')

        return User(user_object)

    @classmethod
    def from_login(self, login):
        user_object = db.users.find_one({
            'login': login
        })
        if not user_object:
            raise Exception('Пользователь с таким логином не найден')

        return User(user_object)
