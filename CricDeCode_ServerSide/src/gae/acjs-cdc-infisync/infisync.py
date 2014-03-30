import webapp2
import json
import time
import urllib
import urllib2
from google.appengine.ext import ndb

class infisync(ndb.Model):
    autorenewing = ndb.IntegerProperty(indexed=False)
    initiation_ts_msec = ndb.IntegerProperty(indexed=False)
    order_id = ndb.StringProperty(indexed=True)
    sign = ndb.StringProperty(indexed=False)
    token = ndb.StringProperty(indexed=False)
    user_id = ndb.StringProperty(indexed=True)
    validuntil_ts_msec = ndb.IntegerProperty(indexed=True)
    not_valid = ndb.IntegerProperty(indexed=True)

class infisync_insert(webapp2.RequestHandler):

    def post(self):

        self.response.headers['Content-Type'] = 'text/plain'

        infisync_obj = infisync()
        infisync_obj.autorenewing = int(self.request.get('autorenewing'))
        infisync_obj.initiation_ts_msec = int(self.request.get('initiation_ts_msec'))
        infisync_obj.order_id = self.request.get('order_id')
        infisync_obj.sign = self.request.get('sign')
        infisync_obj.token = self.request.get('token')
        infisync_obj.user_id = self.request.get('user_id')
        infisync_obj.validuntil_ts_msec = int(self.request.get('validuntil_ts_msec'))
        infisync_obj.not_valid = int(self.request.get('not_valid'))


        obj_list = infisync.query(
        ndb.AND(
        infisync.user_id == infisync_obj.user_id,
        infisync.order_id == infisync_obj.order_id
        )).fetch()
        if(len(obj_list) == 0):
            infisync_obj.put()
            self.response.write('1 row inserted')
        else:
            self.response.write('row already exists')

class infisync_retrieve(webapp2.RequestHandler):

    def post(self):
        self.response.headers['Content-Type'] = 'text/plain'
        uid = self.request.get('user_id')
        obj_list = infisync.query(ndb.AND(infisync.user_id == uid,infisync.not_valid == 0)).order(-infisync.validuntil_ts_msec).fetch(1)
        json_obj = {}
        if(len(obj_list) == 0):
            json_obj["status"] = 0
        else:
            json_obj["status"] = 1
        self.response.write(json.dumps(json_obj))

class infisync_check(webapp2.RequestHandler):

    def post(self):
        self.response.headers['Content-Type'] = 'text/plain'
        uid = self.request.get('user_id')
        obj_list = infisync.query(ndb.AND(infisync.user_id == uid,infisync.not_valid == 0)).order(-infisync.validuntil_ts_msec).fetch(1)
        if(len(obj_list) != 0):
            obj = obj_list[0]
            url = "http://acjs-cdc-andro.appspot.com/retrieve_wo_json"
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
                values['product_id'] = 'sub_infi_sync'
                values['reg_ids'] = regids_str

                data = urllib.urlencode(values)
                req = urllib2.Request(url, data)
                response = urllib2.urlopen(req)
                json_str = response.read()
                json_obj = json.loads(json_str)

                if(json_obj['status'] == 1):
                    infisync_obj = infisync()
                    infisync_obj.user_id = uid
                    infisync_obj.autorenewing = json_obj["autorenewing"]
                    infisync_obj.initiation_ts_msec = json_obj["initiation_ts_msec"]
                    infisync_obj.not_valid = json_obj["not_valid"]
                    infisync_obj.order_id = json_obj["order_id"]
                    infisync_obj.sign = json_obj["sign"]
                    infisync_obj.token = json_obj["token"]
                    infisync_obj.validuntil_ts_msec = json_obj["validuntil_ts_msec"]
                    infisync_obj.put()
                if(json_obj['status'] == 0):
                    obj.not_valid = 1
                    obj.put()
        else:
            json_obj = {}
            json_obj['status'] = 0
            self.response.write(json.dumps(json_obj))

application = webapp2.WSGIApplication([
    ('/insert', infisync_insert),('/retrieve', infisync_retrieve),('/check',infisync_check),
], debug=True)
