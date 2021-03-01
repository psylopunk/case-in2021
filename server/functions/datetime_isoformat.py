from datetime import datetime, timezone

format = '%Y-%m-%dT%H:%M:%SZ'

def to_isoformat(dateobj):
    return dateobj.astimezone(
        timezone.utc
    ).strftime(
        format
    )

def from_isoformat(datestr):
    return datetime.strptime(
        datestr,
        format
    ).replace(tzinfo=timezone.utc)
