from storage import db
from models import User
from hashlib import sha256
from uuid import uuid4

def create_user(full_name: str, login: str, password: str, parent: User=None):
    if parent:
        if parent.get_depth() >= 2:
            raise Exception('Новые сотрудники не могут быть кураторами')

    if db.users.count_documents({
        'login': login
    }):
        raise Exception('Пользователь с таким логином уже существует')

    user_obj = {
        'id': f'{uuid4()}',
        'full_name': full_name,
        'login': login,
        'password': sha256(password.encode('utf8')).digest(),
        'parent_id': parent.id if parent else None,
        'score': 0,
        'action': None,
    }
    r = db.users.insert_one(user_obj)
    user_obj['_id'] = r.inserted_id

    return User(user_obj)
