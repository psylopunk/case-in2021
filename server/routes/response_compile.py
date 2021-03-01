from storage import db
from config import DEBUG_ENABLED
from functions import camel_case
from .valid_fields import valid_fields
from sanic import response
import functools
import sys, os

headers = {
    "Origin": "*",
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Methods": "GET, POST, PATCH, PUT, DELETE, OPTIONS",
    "Access-Control-Allow-Headers": "Origin, Content-Type, X-Auth-Token, Authorization",
    "Access-Control-Allow-Credentials": "true",
    "Referrer-Policy": "origin"
}

def response_compile(required_fields=[]):
    def decorator(f):
        @functools.wraps(f)
        async def decorated_function(request, *args, **kwargs):
            if request.method == "OPTIONS":
                return response.empty(status=204, headers=headers)

            json = {}
            try:
                for key in request.json:
                    json[camel_case(key)] = request.json[key]
            except:
                pass
            try:
                for key in request.args:
                    json[camel_case(key)] = request.args[key][0]
            except:
                pass

            json["_ip"] = (request.headers['X-Forwarded-for'] if 'X-Forwarded-for' in request.headers else None) \
                or request.remote_addr or request.ip

            print('json', json)

            try:
                valid_fields(
                    fields=required_fields,
                    request_data=json
                )
            except Exception as e:
                return response.json({
                    'error': f'{e}'
                })

            if DEBUG_ENABLED:
                res = await f(request, json=json, *args, **kwargs)
                if isinstance(res, response.HTTPResponse):
                    return res
                else:
                    if type(res) == dict or type(res) == list:
                        return response.json(res, headers=headers)
                    else:
                        return response.text(res, headers=headers)
            else:
                try:
                    res = await f(request, json=json, *args, **kwargs)
                    if isinstance(res, response.HTTPResponse):
                        return res
                    else:
                        if type(res) == dict or type(res) == list:
                            return response.json(res, headers=headers)
                        else:
                            return response.text(res, headers=headers)
                except Exception as e:
                    exc_type, exc_obj, exc_tb = sys.exc_info()

                    try:
                        filename = os.path.split(exc_tb.tb_frame.f_code.co_filename)[1]
                    except:
                        filename = None

                    return response.json({
                        'error': f'{e}'
                    }, status=200, headers=headers)
        return decorated_function
    return decorator
