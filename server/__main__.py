from storage import db
from routes import app
from functions import create_user

if __name__ == '__main__':
    if not db.users.count_documents({}):
        create_user(
            'admin',
            'admin',
            'admin'
        )

    app.run(host='0.0.0.0', port=8787)
