import webapp2
import json
from google.appengine.ext import ndb

class ads(ndb.Model):
    order_id = ndb.StringProperty(indexed=False)
    purchasetime = ndb.IntegerProperty(indexed=False)
    sign = ndb.StringProperty(indexed=False)
    token = ndb.StringProperty(indexed=False)
    user_id = ndb.StringProperty(indexed=True)

class ads_insert(webapp2.RequestHandler):

    def post(self):

        self.response.headers['Content-Type'] = 'text/plain'

        ads_obj = ads()
        ads_obj.order_id = self.request.get('order_id')
        ads_obj.purchasetime = int(self.request.get('purchasetime'))
        ads_obj.sign = self.request.get('sign')
        ads_obj.token = self.request.get('token')
        ads_obj.user_id = self.request.get('user_id')

        obj_list = ads.query(ads.user_id == ads_obj.user_id).fetch()
        if(len(obj_list) == 0):
            ads_obj.put()
	json_obj = {}
        json_obj["status"] = 1
        self.response.write(json.dumps(obj))

class ads_retrieve(webapp2.RequestHandler):

    def post(self):

	self.response.headers['Content-Type'] = 'text/plain'

	uid = self.request.get('user_id')
	obj_list = usr.query(usr.user_id == uid).fetch()
    json_obj = {}
    if(len(obj_list) == 0):
        json_obj["status"] = 0
        self.response.write(json.dumps(obj))
    else:
        json_obj["status"] = 1
        self.response.write(json.dumps(obj))

application = webapp2.WSGIApplication([
    ('/insert', ads_insert),('/retrieve', ads_retrieve)
], debug=True)
