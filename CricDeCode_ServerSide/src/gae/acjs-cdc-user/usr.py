import webapp2
import json
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
        usr_obj.first_name = self.request.get('first_name')
        usr_obj.os = int(self.request.get('os'))
        usr_obj.last_name = self.request.get('last_name')
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


application = webapp2.WSGIApplication([('/insert', usr_insert)], debug=True)
