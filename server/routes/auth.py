from .base import app
from .response_compile import response_compile
from models import User

@app.route('/api/auth.signIn', methods=['POST'])
@response_compile(required_fields=['login', 'password'])
async def sign_in(request, json={}, *args, **kwargs):
    user = User.from_login(json['login'])

    token = user.handle_auth(json['password'])

    childs = user.get_childs()

    return {
        'profile': user.get_json(),
        'token': token,
        'childs': [
            child.get_json() for child in childs
        ],
        'depth': user.get_depth()
    }
