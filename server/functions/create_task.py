from storage import db
from models import User, Task
from uuid import uuid4
from datetime import datetime

def create_task(
    employer: User,
    employee: User,
    title: str,
    difficulty: float=0.5,
    description: str=None,
    deadline: datetime=None
):
    if not employee.parent_id == employer.id:
        raise Exception('Пользователь не является Вашим подчиненным')

    if not employer.get_depth() == 1:
        raise Exception('У Вас нет права на создание задачи')

    task_object = {
        'id': f'{uuid4()}',
        'title': title,
        'description': description,
        'difficulty': difficulty,
        'deadline': deadline,
        'employer_id': employer.id,
        'employee_id': employee.id,
        'status': 'created',
        'modified': datetime.now()
    }
    r = db.tasks.insert_one(task_object)
    task_object['_id'] = r.inserted_id

    return Task(task_object)
