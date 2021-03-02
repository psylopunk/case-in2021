from .tasks import app
from storage import db
from .response_compile import response_compile
from models import User, Task
from functions import load_auth, from_isoformat, to_isoformat
from functions import send_message as _send_message
from sanic import response
from datetime import datetime

@app.route('/api/chatbot.getMessages', methods=['GET'])
@response_compile()
async def get_messages(request, json={}):
    user = load_auth(request)

    return [
        {
            'id': message['id'],
            'message': message['message'],
            'keyboard': message['keyboard'],
            'incoming': message['incoming'],
            'created': to_isoformat(message['_id'].generation_time),
        } for message in db.messages.find({
            'user_id': user.id
        }).sort("id", -1).skip(
            int(json["offset"]) if "offset" in json else 0
        ).limit(
            int(json["limit"]) if "limit" in json else 100
        )
    ]

@app.route('/api/chatbot.sendMessage', methods=['POST'])
@response_compile(required_fields=['message'])
async def send_message(request, json={}):
    user = load_auth(request)

    _send_message(
        user,
        json['message'],
        incoming=True
    )

    return response.empty(status=200)
