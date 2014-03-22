import webapp2
from google.appengine.ext import ndb
class regids(ndb.Model):
    user_id = ndb.StringProperty(indexed=True)
    gcm_id = ndb.StringProperty(indexed=True)

class regids_insert(webapp2.RequestHandler):
    def post(self):
        self.response.headers['Content-Type'] = 'text/plain'
        regids_obj = regids()
        regids_obj.user_id = self.request.get('user_id')
        regids_obj.gcm_id = self.request.get('gcm_id')
        obj_list = regids.query(ndb.OR(regids.user_id == regids_obj.user_id,regids.gcm_id == regids_obj.gcm_id)).fetch()
        flag=True
        if(len(obj_list)==0):
            regids_obj.put()
            self.response.write('1 row inserted')
            flag=False
        if(flag):
            for obj in obj_list:
                if(obj.user_id == regids_obj.user_id and obj.gcm_id == regids_obj.gcm_id):
                    self.response.write('row already exists')
                    flag=False
                    break
        if(flag):
            for obj in obj_list:
                if(obj.gcm_id == regids_obj.gcm_id):
                    obj.user_id = regids_obj.user_id
                    obj.put()
                    flag=False
                    self.response.write('1 row updated')
        if(flag):
            regids_obj.put()
            self.response.write('1 row inserted')

class regids_retrieve(webapp2.RequestHandler):
    def post(self):
        user_id = self.request.get('user_id')
        obj_list = regids.query(regids.user_id == user_id).fetch()
        regids_str = ''
        for obj in obj_list:
            regids_str = regids_str + obj.gcm_id + ' '
        regids_str = regids_str.strip()
        self.response.write(regids_str)

application = webapp2.WSGIApplication([('/insert', regids_insert),('/retrieve', regids_retrieve),
], debug=True)
