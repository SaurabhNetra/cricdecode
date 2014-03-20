import webapp2
from google.appengine.ext import ndb

class infi(ndb.Model):
    autorenewing = ndb.IntegerProperty(indexed=False)
    initiation_ts_msec = ndb.IntegerProperty(indexed=False)
    order_id = ndb.StringProperty(indexed=True)
    sign = ndb.StringProperty(indexed=False)
    token = ndb.StringProperty(indexed=False)
    user_id = ndb.StringProperty(indexed=True)
    validuntil_ts_msec = ndb.IntegerProperty(indexed=False)

class infi_insert(webapp2.RequestHandler):

    def post(self):

        self.response.headers['Content-Type'] = 'text/plain'

        infi_obj = infi()
        infi_obj.autorenewing = int(self.request.get('autorenewing'))
        infi_obj.initiation_ts_msec = int(self.request.get('initiation_ts_msec'))
        infi_obj.order_id = self.request.get('order_id')
        infi_obj.sign = self.request.get('sign')
        infi_obj.token = self.request.get('token')
        infi_obj.user_id = self.request.get('user_id')
        infi_obj.validuntil_ts_msec = int(self.request.get('validuntil_ts_msec'))


        obj_list = infi.query(
        ndb.AND(
        infi.user_id == infi_obj.user_id,
        infi.order_id == infi_obj.order_id
        )).fetch()
        if(len(obj_list) == 0):
            infi_obj.put()
            self.response.write('1 row inserted')
        else:
            self.response.write('row already exists')


application = webapp2.WSGIApplication([
    ('/', infi_insert),
], debug=True)
