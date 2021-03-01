from storage import db
from .auth import app
from .response_compile import response_compile
from models import User
from functions import load_auth, create_user
from sanic import response

@app.route('/api/account.getChilds', methods=['GET'])
@response_compile()
async def get_childs(request, json={}):
    user = load_auth(request)

    childs = user.get_childs()

    return {
        'childs': [
            child.get_json() for child in childs
        ],
        'depth': user.get_depth()
    }

@app.route('/api/account.addEmployee', methods=['POST'])
@response_compile(required_fields=['login', 'password', 'fullName'])
async def add_employee(request, json={}):
    user = load_auth(request)

    created_user = create_user(
        json['fullName'],
        json['login'],
        json['password'],
        parent=user
    )

    return created_user.get_json()

@app.route('/api/account.removeEmployee', methods=['POST'])
@response_compile(required_fields=['userId'])
async def remove_employee(request, json={}):
    user = load_auth(request)
    selected_user = User.from_id(json['userId'])

    depth = user.get_depth()
    if depth in [1, 2] and not selected_user.parent_id == user.id:
        raise Exception('Вы не можете удалить этого пользователя')

    selected_user.drop()
    return response.empty(status=200)
