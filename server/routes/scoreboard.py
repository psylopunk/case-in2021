from storage import db
from .chatbot import app
from .response_compile import response_compile
from models import User
from functions import load_auth, from_isoformat, to_isoformat
from functions import send_message as _send_message
from sanic import response
from datetime import datetime

@app.route('/api/scoreboard.get', methods=['GET'])
@response_compile()
async def _get(request, json={}):
    user = load_auth(request)

    if 'userId' in json:
        user = User.from_id(json['userId'])

    users, _users = [], [
        User(user_obj) for user_obj in db.users.find({})
    ]

    for i, _user in enumerate(_users):
        if _user.get_depth() != 2:
            continue

        _user.score = _user.calculate_score()
        users.append(_user)

    if not users:
        raise Exception('Недостаточно данных')

    scoreboard = [x for x in reversed(
        sorted(users, key=lambda x: x.score)
    )]
    for i, _user in enumerate(scoreboard):
        _user.place = i + 1

    try:
        relevant_number = next(i for i, _user in enumerate(scoreboard) if user == _user) # +1
    except Exception as e:
        relevant_number = None

    formatted_scoreboard = []
    if relevant_number:
        for _user in scoreboard[
            (relevant_number - 14) if (relevant_number - 14) >= 0 else None:
            (relevant_number + 14) if (relevant_number + 14) <= len(scoreboard) else None
        ]:
            user_obj = _user.get_json()
            user_obj['score'] = _user.score
            user_obj['place'] = _user.place
            formatted_scoreboard.append(user_obj)
    else:
        for _user in scoreboard[:100]:
            user_obj = _user.get_json()
            user_obj['score'] = _user.score
            user_obj['place'] = _user.place
            formatted_scoreboard.append(user_obj)

    return {
        'place': relevant_number + 1 if relevant_number else -1,
        'scoreboard': formatted_scoreboard
    }
