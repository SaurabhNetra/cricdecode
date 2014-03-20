import webapp2
from google.appengine.ext import ndb

class sync(ndb.Model):
    autorenewing = ndb.IntegerProperty(indexed=False)
    initiation_ts_msec = ndb.IntegerProperty(indexed=False)
    order_id = ndb.StringProperty(indexed=True)
    sign = ndb.StringProperty(indexed=False)
    token = ndb.StringProperty(indexed=False)
    user_id = ndb.StringProperty(indexed=True)
    validuntil_ts_msec = ndb.IntegerProperty(indexed=False)

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

application = webapp2.WSGIApplication([
    ('/', sync_insert),
], debug=True)
