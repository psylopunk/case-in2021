def valid_fields(
    fields: list,
    request_data: dict=None
):
    if not request_data and fields:
        raise Exception("Empty request data")

    for key in fields: # checking for arguments
        if key not in request_data:
            raise Exception(f"Field '{key}' is required")
        if request_data[key] == '':
            raise Exception(f"Field '{key}' is empty")
