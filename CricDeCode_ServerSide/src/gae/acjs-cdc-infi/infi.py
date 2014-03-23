import webapp2
import time
import urllib
import urllib2
import json
from google.appengine.ext import ndb

class infi(ndb.Model):
    autorenewing = ndb.IntegerProperty(indexed=False)
    initiation_ts_msec = ndb.IntegerProperty(indexed=False)
    order_id = ndb.StringProperty(indexed=True)
    sign = ndb.StringProperty(indexed=False)
    token = ndb.StringProperty(indexed=False)
    user_id = ndb.StringProperty(indexed=True)
    validuntil_ts_msec = ndb.IntegerProperty(indexed=True)
    not_valid = ndb.IntegerProperty(indexed=True)

class infi_insert(webapp2.RequestHandler):

    def post(self):

        self.response.headers['Content-Type'] = 'text/plain'

        infi_obj = infi()
        infi_obj.autorenewing = int(self.request.get('autorenewing'))
        infi_obj.initiation_ts_msec = int(self.request.get('initiation_ts_msec'))
        infi_obj.order_id = self.request.get('order_id')
        infi_obj.sign = self.request.get('sign')
        infi_obj.token = self.request.get('token')
        infi_obj.user_id = self.request.get('user_id')
        infi_obj.validuntil_ts_msec = int(self.request.get('validuntil_ts_msec'))
        infi_obj.not_valid = int(self.request.get('not_valid'))

        obj_list = infi.query(
        ndb.AND(
        infi.user_id == infi_obj.user_id,
        infi.order_id == infi_obj.order_id
        )).fetch()
        if(len(obj_list) == 0):
            infi_obj.put()
            self.response.write('1 row inserted')
        else:
            self.response.write('row already exists')

class infi_retrieve(webapp2.RequestHandler):

    def post(self):
        self.response.headers['Content-Type'] = 'text/plain'
        uid = self.request.get('user_id')
        obj_list = infi.query(ndb.AND(infi.user_id == uid,infi.not_valid == 0)).order(-infi.validuntil_ts_msec).fetch(1)
        json_obj = {}
        if(len(obj_list) == 0):
            json_obj["status"] = 0
        else:
            json_obj["status"] = 1
        self.response.write(json.dumps(json_obj))

class infi_check(webapp2.RequestHandler):

    def post(self):
        self.response.headers['Content-Type'] = 'text/plain'
        uid = self.request.get('user_id')
        obj_list = infi.query(ndb.AND(infi.user_id == uid,infi.not_valid == 0)).order(-infi.validuntil_ts_msec).fetch(1)
        if(len(obj_list) != 0):
            obj = obj_list[0]
            url = "http://acjs-cdc-andro.appspot.com/retrieve"
            values = {}
            values['user_id'] = uid
            data = urllib.urlencode(values)
            req = urllib2.Request(url, data)
            response = urllib2.urlopen(req)
            regids_str = response.read()
            now = int(round(time.time()*1000))
            if now < obj.validuntil_ts_msec:
                json_obj = {}
                json_obj["status"] = 1
                json_obj["reg_ids"] = regids_str
                self.response.write(json.dumps(json_obj))
            else:
                url = "http://acjs.azurewebsites.net/acjs/CDCSubscriptionPurchaseChk_vGAE.php"
                values = {}
                values['order_id'] = obj.order_id
                values['sign'] = obj.sign
                values['token'] = obj.token
                values['product_id'] = 'sub_infi'
                values['reg_ids'] = regids_str

                data = urllib.urlencode(values)
                req = urllib2.Request(url, data)
                response = urllib2.urlopen(req)
                json_str = response.read()
                json_obj = json.loads(json_str)

                if(json_obj['status'] == 1):
                    infi_obj = infi()
                    infi_obj.user_id = uid
                    infi_obj.autorenewing = json_obj["autorenewing"]
                    infi_obj.initiation_ts_msec = json_obj["initiation_ts_msec"]
                    infi_obj.not_valid = json_obj["not_valid"]
                    infi_obj.order_id = json_obj["order_id"]
                    infi_obj.sign = json_obj["sign"]
                    infi_obj.token = json_obj["token"]
                    infi_obj.validuntil_ts_msec = json_obj["validuntil_ts_msec"]
                    infi_obj.put()
                else:
                    obj.not_valid = 1
                    obj.put()
        else:
            json_obj = {}
            json_obj['status'] = 0
            self.response.write(json.dumps(json_obj))




application = webapp2.WSGIApplication([
    ('/insert', infi_insert),('/retrieve', infi_retrieve),('/check',infi_check),
], debug=True)
