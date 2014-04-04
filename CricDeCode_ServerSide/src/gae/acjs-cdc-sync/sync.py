import webapp2
import json
import time
import urllib
import urllib2
from google.appengine.ext import ndb

class sync(ndb.Model):
    autorenewing = ndb.IntegerProperty(indexed=False)
    initiation_ts_msec = ndb.IntegerProperty(indexed=False)
    order_id = ndb.StringProperty(indexed=True)
    sign = ndb.StringProperty(indexed=False)
    token = ndb.StringProperty(indexed=False)
    user_id = ndb.StringProperty(indexed=True)
    validuntil_ts_msec = ndb.IntegerProperty(indexed=True)
    not_valid = ndb.IntegerProperty(indexed=True)

class sync_insert(webapp2.RequestHandler):

    def post(self):

        self.response.headers['Content-Type'] = 'text/plain'
        uid = self.request.get('user_id')
        obj_list = sync.query(sync.user_id == uid).fetch()
        if(len(obj_list) == 0):
            sync_obj = sync()
            sync_obj.autorenewing = int(self.request.get('autorenewing'))
            sync_obj.initiation_ts_msec = int(self.request.get('initiation_ts_msec'))
            sync_obj.order_id = self.request.get('order_id')
            sync_obj.sign = self.request.get('sign')
            sync_obj.token = self.request.get('token')
            sync_obj.user_id = self.request.get('user_id')
            sync_obj.validuntil_ts_msec = int(self.request.get('validuntil_ts_msec'))
            sync_obj.not_valid = 0
            sync_obj.put()
            self.response.write('{"status" : 1}')
        else:
            sync_obj = obj_list[0]
            sync_obj.autorenewing = int(self.request.get('autorenewing'))
            sync_obj.initiation_ts_msec = int(self.request.get('initiation_ts_msec'))
            sync_obj.order_id = self.request.get('order_id')
            sync_obj.sign = self.request.get('sign')
            sync_obj.token = self.request.get('token')
            sync_obj.user_id = self.request.get('user_id')
            sync_obj.validuntil_ts_msec = int(self.request.get('validuntil_ts_msec'))
            sync_obj.not_valid = 0
            sync_obj.put()
            self.response.write('{"status" : 1}')

class sync_retrieve(webapp2.RequestHandler):

    def post(self):
        self.response.headers['Content-Type'] = 'text/plain'
        uid = self.request.get('user_id')
        obj_list = sync.query(sync.user_id == uid).fetch()
        json_obj = {}
        if(len(obj_list) == 0):
            json_obj["status"] = 0
        else:
            if(obj_list[0].not_valid==1):
                json_obj["status"] = 0
            else:
                json_obj["status"] = 1
        self.response.write(json.dumps(json_obj))

class sync_check(webapp2.RequestHandler):

    def post(self):
        self.response.headers['Content-Type'] = 'text/plain'
        uid = self.request.get('user_id')
        token = self.request.get('token')
        sign = self.request.get('sign')
        orderid = self.request.get('orderId')
        obj_list = sync.query(sync.user_id == uid).fetch()
        ret_status = {}
        url = "http://acjs-cdc-andro.appspot.com/retrieve_wo_json"
        values = {}
        values['user_id'] = uid
        data = urllib.urlencode(values)
        req = urllib2.Request(url, data)
        response = urllib2.urlopen(req)
        regids_str = response.read()
        if(len(obj_list) != 0):
            obj = obj_list[0]
            now = int(round(time.time()*1000))
            if now < obj.validuntil_ts_msec:
                ret_status["status"] = 1
                ret_status["reg_ids"] = regids_str
            else:                
                url = "http://acjs.azurewebsites.net/acjs/CDCSubscriptionPurchaseChk_vGAE.php"
                values = {}
                values['token'] = obj.token
                values['product_id'] = 'sub_sync'
                values['reg_ids'] = regids_str                
                values['user_id'] = uid
                data = urllib.urlencode(values)
                req = urllib2.Request(url, data)
                response = urllib2.urlopen(req)
                json_str = response.read()
                json_obj = json.loads(json_str)
                if(json_obj['status'] == 1):                   
                    obj.user_id = uid
                    obj.autorenewing = json_obj["auto_ren"]
                    obj.initiation_ts_msec = json_obj["init_ts"]
                    obj.not_valid = 0
                    obj.order_id = orderid
                    obj.sign = sign
                    obj.token = token
                    obj.validuntil_ts_msec = json_obj["valid_ts"]
                    obj.put()
                    ret_status["status"] = 1
                    ret_status['reg_ids'] = regids_str
                if(json_obj['status'] == 0):
                    obj.not_valid = 1
                    obj.put()
                    ret_status["status"] = 0
                if(json_obj['status'] == 2):
                    ret_status["status"] = 2
                 
        else:
            url = "http://acjs.azurewebsites.net/acjs/CDCSubscriptionPurchaseChk_vGAE.php"
            values = {}
            values['token'] = token
            values['product_id'] = 'sub_sync'
            values['reg_ids'] = regids_str                
            values['user_id'] = uid
            data = urllib.urlencode(values)
            req = urllib2.Request(url, data)
            response = urllib2.urlopen(req)
            json_str = response.read()
            json_obj = json.loads(json_str)
            if(json_obj['status'] == 1):
                obj = sync()                 
                obj.user_id = uid
                obj.autorenewing = json_obj["auto_ren"]
                obj.initiation_ts_msec = json_obj["init_ts"]
                obj.not_valid = 0
                obj.order_id = orderid
                obj.sign = sign
                obj.token = token
                obj.validuntil_ts_msec = json_obj["valid_ts"]
                obj.put()
                ret_status["status"] = 1
                ret_status['reg_ids'] = regids_str
                if(json_obj['status'] == 0):
                    obj.not_valid = 1
                    obj.put()
                    ret_status["status"] = 0
                if(json_obj['status'] == 2):
                    ret_status["status"] = 2
                      
        self.response.write(json.dumps(ret_status))

application = webapp2.WSGIApplication([
    ('/insert', sync_insert),('/retrieve', sync_retrieve),('/check',sync_check),
], debug=True)
