from storage import db
from functions.datetime_isoformat import to_isoformat, from_isoformat
from .user import User

class Task:
    def get_json(self):
        return {
            'id': self.id,
            'employee': self.employee.get_json(),
            'employer': self.employer.get_json(),
            'title': self.title,
            'description': self.description,
            'difficulty': self.difficulty,
            'deadline': to_isoformat(self.deadline) if self.deadline else None,
            'status': self.status,
            'modified': to_isoformat(self.modified)
        }

    def __init__(self, task_object):
        self._id = task_object['_id']
        self.id = task_object['id']

        self.employer = User.from_id(task_object['employer_id'])
        self.employee = User.from_id(task_object['employee_id'])

        self.title = task_object['title']
        self.description = task_object['description']

        self.difficulty = task_object['difficulty']

        self.deadline = task_object['deadline']

        self.status = task_object['status'] # created, accepted, rejected, completed
        self.modified = task_object['modified']

    @classmethod
    def from_id(self, id):
        task_object = db.tasks.find_one({
            'id': id
        })
        if not task_object:
            raise Exception('Задача не найдена')

        return Task(task_object)
