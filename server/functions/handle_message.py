from storage import db
from models import User
from functions import send_message

def send_welcome(user):
    user.drop_action()
    send_message(
        user,
        'Вы пролили кофе на своем рабочем месте. На что попал напиток?',
        keyboard=['Клавиатура', 'Сист. блок', 'Удлинитель'],
        action='input_deviceName'
    )

def handle_message(user: User, message: str):
    if message in ['/start', 'Домой']:
        send_welcome(user)
        return True

    if user.action:
        action = user.action
        user.drop_action()
        if action.find('input_') == 0:
            key = action.split('_')[1]

            if key == 'deviceName':
                if message == 'Клавиатура':
                    send_message(
                        user,
                        'На клавиатуру от компьютера?',
                        keyboard=['Да', 'Нет'],
                        action='input_submitKeyboardType'
                    )
                    return True
                elif message == 'Сист. блок':
                    send_message(
                        user,
                        """Следуй этой инструкции

1) Убери следы кофе с корпуса
2) Полностью отключи компьютер для твоей безопасности
3) Разбери компьютер и посмотри, куда именно попал напиток

Самым оптимальным вариантом для тебя является обратиться к куратору и объяснить ситуацию. Ты уже обратился к своему наставнику?""",
                        keyboard=['Да', 'Нет'],
                        action='input_submitHeadConnect'
                    )
                    return True
                elif message == 'Удлинитель':
                    send_message(
                        user,
                        """Следуй этой инструкции

1) Выключи удлинитель из розетки
2) Вызови мастера электрика, чтобы безопасно исправить полученную проблему

В случае серьезных проблем, звонить 101"""
                    )
                    send_welcome(user)  
                    return True
            elif key == 'submitKeyboardType':
                if message in ['Да', 'Нет']:
                    send_message(
                        user,
                        'Вы протерли стол и кнопки клавиатуры (вынули, почистили, высушили, засунули). Исправно ли работает устройство?',
                        keyboard=['Да', 'Нет'],
                        action='input_submitDeviceOnline'
                    )
                    return True
            elif key == 'submitDeviceOnline':
                if message == 'Да':
                    send_message(
                        user,
                        'Отлично! Советую так больше не делать'
                    )
                    send_welcome(user)
                    return True
                elif message == 'Нет':
                    send_message(
                        user,
                        """Следуйте следующей инструкции:

1) Выключи клавиатуру, отключив провод
2) Переверни устройство и отверткой открути нижнюю крышку. Винтики не теряй. Сними крышку
3) Аккуратно достань мембану и протри сухой чистой тряпочкой электропроводящую пленку
4) Собери все обратно. Окончательно просуши. Запомни, все внутренности должны быть сухими, дай им время обсохнуть
5) Подожди несколько минут и попробуй включить устройство

Клавиатура заработала?""",
                        keyboard=['Да', 'Нет'],
                        action='input_submitKeyboardFix1'
                    )
                    return True
            elif key == 'submitKeyboardFix1':
                if message == 'Да':
                    send_message(
                        user,
                        'Отлично! Постарайся больше такого не допускать'
                    )
                    send_welcome(user)
                    return True
                elif message == 'Нет':
                    send_message(
                        user,
                        'В таком случае остается только один выход - сдать устройство в сервис, телефон +74992223322'
                    )
                    send_welcome(user)
                    return True
            elif key == 'submitHeadConnect':
                if message == 'Да':
                    send_message(
                        user,
                        'Ты правильно поступил, но в следующий раз лучше до такого не доводить'
                    )
                    send_welcome(user)
                    return True
                elif message == 'Нет':
                    send_message(
                        user,
                        'Срочно обратись к своему куратору, чтобы он мог тебе помочь'
                    )
                    send_welcome(user)
                    return True

    send_message(
        user,
        'Пользуйтесь только встроенной клавиатурой, Вы возвращены в начало'
    )
    send_welcome(user)
    return True
