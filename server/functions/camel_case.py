def camel_case(value: str):
    upper_indexes = [i for i, e in enumerate(value) if e.isupper() and i]
    value = value.lower()
    for i in upper_indexes:
        value = value[:i] + value[i].upper() + value[i + 1:]
    return value
