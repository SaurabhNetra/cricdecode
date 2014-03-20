import webapp2
from google.appengine.ext import ndb

class infisync(ndb.Model):
    autorenewing = ndb.IntegerProperty(indexed=False)
    initiation_ts_msec = ndb.IntegerProperty(indexed=False)
    order_id = ndb.StringProperty(indexed=True)
    sign = ndb.StringProperty(indexed=False)
    token = ndb.StringProperty(indexed=False)
    user_id = ndb.StringProperty(indexed=True)
    validuntil_ts_msec = ndb.IntegerProperty(indexed=False)

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

application = webapp2.WSGIApplication([
    ('/', infisync_insert),
], debug=True)
