import webapp2
import json
import urllib
import urllib2
import re
import codecs
from google.appengine.ext import ndb
class usr(ndb.Model):
    batting_style = ndb.StringProperty(indexed=False)
    bowling_style = ndb.StringProperty(indexed=False)
    device_no = ndb.IntegerProperty(indexed=False)
    dob = ndb.StringProperty(indexed=False)
    fb_link = ndb.StringProperty(indexed=False)
    first_name = ndb.StringProperty(indexed=False)
    os = ndb.IntegerProperty(indexed=False)
    last_name = ndb.StringProperty(indexed=False)
    nick_name = ndb.StringProperty(indexed=False)
    role = ndb.StringProperty(indexed=False)
    user_id = ndb.StringProperty(indexed=True)
class usr_insert(webapp2.RequestHandler):
    def post(self):
        self.response.headers['Content-Type'] = 'text/plain'
        usr_obj = usr()
        #usr_obj.batting_style = self.request.get('batting_style')
        #usr_obj.bowling_style = self.request.get('bowling_style')
        #usr_obj.device_no = int(self.request.get('device_no'))
        #usr_obj.nick_name = self.request.get('nick_name')
        #usr_obj.role = self.request.get('role')
        usr_obj.batting_style = ""
        usr_obj.bowling_style = ""
        usr_obj.device_no = 1
        usr_obj.dob = self.request.get('dob')
        usr_obj.fb_link = self.request.get('fb_link')
        usr_obj.first_name = self.request.get('f_name')
        usr_obj.os = int(self.request.get('os'))
        usr_obj.last_name = self.request.get('l_name')
        usr_obj.nick_name = ""
        usr_obj.role = ""
        uid = self.request.get('user_id')
        usr_obj.user_id = uid
        url = "http://acjs-cdc-andro.appspot.com/insert"
        values = {
			'gcm_id' : self.request.get('gcm_id'),
			'user_id' : uid
		}
        data = urllib.urlencode(values)
        req = urllib2.Request(url, data)
        response = urllib2.urlopen(req)
        response.read()
        obj_list = usr.query(usr.user_id == usr_obj.user_id).fetch()
        if(len(obj_list) == 0):
            usr_obj.put()

            json_obj={}
            json_obj["status"] = 1
            self.response.write(json.dumps(json_obj))

        else:
            usr_obj = obj_list[0]
            usr_obj.device_no = usr_obj.device_no + 1
            usr_obj.put()

            json_obj = {}
            json_obj["status"] = 2
            json_obj["device_no"] = usr_obj.device_no
            json_obj["role"] = usr_obj.role
            json_obj["batting_style"] = usr_obj.batting_style
            json_obj["bowling_style"] = usr_obj.bowling_style
            json_obj["nick_name"] = usr_obj.nick_name
            self.response.write(json.dumps(json_obj))

class usr_update(webapp2.RequestHandler):
    def post(self):
        user_id = self.request.get('user_id')
        obj_list = usr.query(usr.user_id == user_id).fetch()
        obj = obj_list[0]
        obj.role = self.request.get('role')
        obj.nick_name = self.request.get('nick_name')
        obj.batting_style = self.request.get('batting_style')
        obj.bowling_style = self.request.get('bowling_style')
        obj.put()
        status = {}
        status['status'] = 1
        url = "http://acjs-cdc-andro.appspot.com/retrieve_wo_json"
        values = {}
        values['user_id'] = user_id
        data = urllib.urlencode(values)
        req = urllib2.Request(url, data)
        response = urllib2.urlopen(req)
        regids_str = response.read()
        status['reg_ids'] = regids_str             
        self.response.write(status)


class pingmig(webapp2.RequestHandler):
    def post(self):
	url = "http://api.stackmob.com/serverdbremoveads"
        req = urllib2.Request(url)
        req.add_header('Accept','application/vnd.stackmob+json; version=1' )
        req.add_header('X-StackMob-API-Key', 'edc181a0-1ada-4339-a7ab-890e08c67839')
        req.add_header('Content-Type','application/json')
        response = urllib2.urlopen(req)
        res = response.read()
        datarr = {}
        datarr = json.loads(res)
        self.response.write(len(datarr))        
	url = "http://acjs-cdc-ads.appspot.com/insert"
        for data in datarr:
            values = {}
            values['order_id'] = data['order_id']
            values['sign'] = data['sign']
            values['token'] = data['token']
            values['user_id'] = data['user_id']
            values['purchasetime'] = data['purchasetime']
            data = urllib.urlencode(values)
            req = urllib2.Request(url, data)
            response = urllib2.urlopen(req)

application = webapp2.WSGIApplication([('/insert', usr_insert),('/update',usr_update),('/pingmig',pingmig)
], debug=True)
