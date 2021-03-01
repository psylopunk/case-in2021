from storage import db
from models import User
from sanic.request import Request

def load_auth(request: Request):
    if "authorization" not in request.headers:
        raise Exception("Необходима авторизация")

    token = request.headers["authorization"]
    if not token:
        raise Exception("Необходима авторизация")

    session = db.sessions.find_one({
        "token": token
    })
    if not session:
        raise Exception("Необходима авторизация")

    return User.from_id(session['user_id'])
