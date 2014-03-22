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

        sync_obj = sync()
        sync_obj.autorenewing = int(self.request.get('autorenewing'))
        sync_obj.initiation_ts_msec = int(self.request.get('initiation_ts_msec'))
        sync_obj.order_id = self.request.get('order_id')
        sync_obj.sign = self.request.get('sign')
        sync_obj.token = self.request.get('token')
        sync_obj.user_id = self.request.get('user_id')
        sync_obj.validuntil_ts_msec = int(self.request.get('validuntil_ts_msec'))
        sync_obj.not_valid = int(self.request.get('not_valid'))


        obj_list = sync.query(
        ndb.AND(
        sync.user_id == sync_obj.user_id,
        sync.order_id == sync_obj.order_id
        )).fetch()
        if(len(obj_list) == 0):
            sync_obj.put()
            self.response.write('1 row inserted')
        else:
            self.response.write('row already exists')

class sync_retrieve(webapp2.RequestHandler):

    def post(self):
        self.response.headers['Content-Type'] = 'text/plain'
        uid = self.request.get('user_id')
        obj_list = sync.query(ndb.AND(sync.user_id == uid,sync.not_valid == 0)).order(-sync.validuntil_ts_msec).fetch(1)
        json_obj = {}
        if(len(obj_list) == 0):
            json_obj["status"] = 0
        else:
            json_obj["status"] = 1
        self.response.write(json.dumps(json_obj))

class sync_check(webapp2.RequestHandler):

    def post(self):
        self.response.headers['Content-Type'] = 'text/plain'
        uid = self.request.get('user_id')
        obj_list = sync.query(ndb.AND(sync.user_id == uid,sync.not_valid == 0)).order(-sync.validuntil_ts_msec).fetch(1)
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
                url = "Azure url to be inserted"
                values = {}
                values['order_id'] = obj.order_id
                values['sign'] = obj.sign
                values['token'] = obj.token
                values['product_id'] = 'sub_sync'
                values['reg_ids'] = regids_str

                data = urllib.urlencode(values)
                req = urllib2.Request(url, data)
                response = urllib2.urlopen(req)
                json_str = response.read()
                json_obj = json.loads(json_str)

                if(json_obj['status'] == 1):
                    sync_obj = sync()
                    sync_obj.user_id = uid
                    sync_obj.autorenewing = json_obj["autorenewing"]
                    sync_obj.initiation_ts_msec = json_obj["initiation_ts_msec"]
                    sync_obj.not_valid = json_obj["not_valid"]
                    sync_obj.order_id = json_obj["order_id"]
                    sync_obj.sign = json_obj["sign"]
                    sync_obj.token = json_obj["token"]
                    sync_obj.validuntil_ts_msec = json_obj["validuntil_ts_msec"]
                    sync_obj.put()
                else:
                    obj.not_valid = 1
                    obj.put()
        else:
            json_obj = {}
            json_obj['status'] = 0
            self.response.write(json.dumps(json_obj))

application = webapp2.WSGIApplication([
    ('/insert', sync_insert),('/retrieve', sync_retrieve),('/check',sync_check),
], debug=True)
