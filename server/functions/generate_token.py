from random import choices
import string

def generate_token(length: int=32, alphabet: str=f'{string.ascii_letters}{string.digits}'):
    return ''.join([choices(string.ascii_letters)[0] for x in range(length)])
