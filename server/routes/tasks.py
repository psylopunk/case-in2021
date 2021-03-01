from storage import db
from .account import app
from .response_compile import response_compile
from models import User, Task
from functions import load_auth, create_task, from_isoformat
from sanic import response

@app.route('/api/tasks.create', methods=['POST'])
@response_compile(required_fields=[
    'userId', 'title'
])
async def create(request, json={}):
    user = load_auth(request)
    selected_user = User.from_id(json['userId'])

    created_task = create_task(
        user,
        selected_user,
        json['title'],
        difficulty=json['difficulty'] if 'difficulty' in json else 0.5,
        description=json['description'] if 'description' in json else None,
        deadline=from_isoformat(json['deadline']) if 'deadline' in json else None,
    )

    return created_task.get_json()

@app.route('/api/tasks.get', methods=['GET'])
@response_compile()
async def get(request, json={}):
    user = load_auth(request)

    if 'userId' in json:
        selected_user = User.from_id(json['userId'])
        if not selected_user.parent_id == user.id:
            raise Exception('Пользователь не является Вашим подчиненным')
    else:
        selected_user = user

    return [
        Task(task_object).get_json() for task_object in db.tasks.find(
            {
                0: {
                    'employer_id' if selected_user.get_depth() in [0, 1] else 'employee_id': selected_user.id
                } if 'userId' in json else {},
                1: {
                    'employee_id': selected_user.id,
                    'employer_id': user.id
                } if 'userId' in json else {
                    'employer_id': selected_user.id
                },
                2: {
                    'employee_id': selected_user.id
                }
            }[user.get_depth()]
        ).sort("modified", -1).skip(
            json["offset"] if "offset" in json else 0
        ).limit(
            json["limit"] if "limit" in json else 100
        )
    ]
