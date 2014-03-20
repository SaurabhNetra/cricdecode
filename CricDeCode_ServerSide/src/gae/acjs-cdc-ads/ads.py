import webapp2
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
            self.response.write('1 row inserted')
        else:
            self.response.write('row already exists')


application = webapp2.WSGIApplication([
    ('/', ads_insert),
], debug=True)
