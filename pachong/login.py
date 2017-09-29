#encoding=utf-8
import requests
import time
import random
from bs4 import BeautifulSoup

INDEX_URL = 'http://www.zhihu.com'
LOGIN_URL = 'http://www.zhihu.com/login/email'
CAPTCHA_URL = 'http://www.zhihu.com/captcha.gif?r='

def gen_time_stamp():
    return str(int(time.time())) + '%03d' % random.randint(0, 999)

def login(username, password, oncaptcha):
    session = requests.session()

    _xsrf = BeautifulSoup(session.get(INDEX_URL).content).find('input', attrs={'name': '_xsrf'})['value']
    data = {
        '_xsrf': _xsrf,
        'email': username,
        'password': password,
        'remember_me': 'true',
        'captcha': oncaptcha(session.get(CAPTCHA_URL + gen_time_stamp()).content)
    }
    resp = session.post(LOGIN_URL, data)
    if 2 != resp.status_code / 100 or u"登陆成功" not in resp.content:
        raise Exception('captcha error.')
    return session
print(login("","",""))