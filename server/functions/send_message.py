from storage import db
from models import User

def send_message(user: User, message: str, keyboard: list=None, action: str=None, data: dict=None, incoming: bool=False):
    db.messages.insert_one({
        'id': db.messages.count_documents({
            'user_id': user.id
        }),
        'user_id': user.id,
        'message': message,
        'keyboard': keyboard,
        'incoming': incoming,
    })

    if action:
        db.users.update_one({
            '_id': user._id,
        }, {
            '$set': {
                'action': action
            }
        })

    if data:
        db.users.update_one({
            '_id': user._id
        }, {
            '$set': {
                'data': data
            }
        })
