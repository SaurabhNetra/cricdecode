import webapp2
from google.appengine.ext import ndb

class regids(ndb.Model):
    user_id = ndb.StringProperty(indexed=True)
    gcm_id = ndb.StringProperty(indexed=True)

class regids_insert(webapp2.RequestHandler):

    def post(self):

        self.response.headers['Content-Type'] = 'text/plain'
        serverdbandroiddevices_obj = regids()
        serverdbandroiddevices_obj.user_id = self.request.get('user_id')
        serverdbandroiddevices_obj.gcm_id = self.request.get('gcm_id')

        obj_list = regids.query(ndb.OR(serverdbandroiddevices.user_id == serverdbandroiddevices_obj.user_id,serverdbandroiddevices.gcm_id == serverdbandroiddevices_obj.gcm_id)).fetch()
        flag=True
        if(len(obj_list)==0):
            serverdbandroiddevices_obj.put()
            self.response.write('1 row inserted')
            flag=False

        if(flag):
            for obj in obj_list:
                if(obj.user_id == serverdbandroiddevices_obj.user_id and obj.gcm_id == serverdbandroiddevices_obj.gcm_id):
                    self.response.write('row already exists')
                    flag=False
                    break
        if(flag):
            for obj in obj_list:
                if(obj.gcm_id == serverdbandroiddevices_obj.gcm_id):
                    obj.user_id = serverdbandroiddevices_obj.user_id
                    obj.put()
                    flag=False
                    self.response.write('1 row updated')
        if(flag):
            serverdbandroiddevices_obj.put()
            self.response.write('1 row inserted')

application = webapp2.WSGIApplication([
    ('/insert', regids_insert),
], debug=True)
