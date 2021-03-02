from storage import db
from models import User

def send_message(user: User, message: str, keyboard: list=None, incoming: bool=False):
    db.messages.insert_one({
        'id': db.messages.count_documents({
            'user_id': user.id
        }),
        'user_id': user.id,
        'message': message,
        'keyboard': keyboard,
        'incoming': incoming,
    })
