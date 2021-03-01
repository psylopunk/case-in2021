from storage import db
from .account import app
from .response_compile import response_compile
from models import User, Task
from functions import load_auth, create_task, from_isoformat
from sanic import response
from datetime import datetime

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

@app.route('/api/tasks.edit', methods=['POST'])
@response_compile(required_fields=['id'])
async def edit(request, json={}):
    user = load_auth(request)
    selected_task = Task.from_id(json['id'])

    if user.get_depth() == 2 and not user.id == selected_task.employee.id:
        raise Exception('Это задание не предназначено для Вас')
    if user.get_depth() == 1 and not user.id == selected_task.employer.id:
        raise Exception('Это задание создали не Вы')

    updates = {}
    for key in [
        'title', 'description', 'difficulty', 'deadline', 'status'
    ]:
        if key in json:
            value = json[key]

            if key == 'deadline':
                try:
                    value = from_isoformat(value)
                except Exception as e:
                    raise Exception('Указан неверный дедлайн')
            elif key == 'status':
                if value not in ['created', 'accepted', 'rejected', 'completed']:
                    raise Exception('Недопустимый статус')

                STATUS_VARIANTS = {'created': 0, 'accepted': 1, 'rejected': 1, 'completed': 2}

                if STATUS_VARIANTS[value] <= STATUS_VARIANTS[selected_task.status]:
                    raise Exception('Задача не может быть изменена в обратную сторону')
            elif key == 'difficulty':
                if not (0 <= value <= 1):
                    raise Exception('Сложность должна быть от 0 до 1')

            if user.get_depth() == 2 and user.id == selected_task.employee.id:
                if key in ['status']:
                    updates[key] = value
            elif (user.get_depth() == 1 and user.id == selected_task.employer.id) or user.get_depth() == 0:
                if key in ['title', 'description', 'difficulty', 'deadline', 'status']:
                    updates[key] = value

            updates[key] = value

    updates['modified'] = datetime.now()
    db.tasks.update_one({
        '_id': selected_task._id
    }, {
        '$set': updates
    })

    return Task.from_id(json['id']).get_json()

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

    query = {
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
    if 'status' in json:
        query['status'] = json['status']

    return [
        Task(task_object).get_json() for task_object in db.tasks.find(
            query
        ).sort("modified", -1).skip(
            json["offset"] if "offset" in json else 0
        ).limit(
            json["limit"] if "limit" in json else 100
        )
    ]
