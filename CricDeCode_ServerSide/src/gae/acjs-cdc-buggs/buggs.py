import webapp2
from google.appengine.ext import ndb
class buggs(ndb.Model):
    app_version_code=ndb.IntegerProperty(indexed=False)
    phone_model=ndb.StringProperty(indexed=False)
    brand=ndb.StringProperty(indexed=False)
    android_version=ndb.StringProperty(indexed=False)
    build=ndb.StringProperty(indexed=False)
    total_mem_size=ndb.StringProperty(indexed=False)
    available_mem_size=ndb.StringProperty(indexed=False)
    a_stack_trace=ndb.TextProperty(indexed=False)
    crash_conf=ndb.StringProperty(indexed=False)
    user_crash_date=ndb.StringProperty(indexed=False)

class buggs_insert(webapp2.RequestHandler):
    def post(self):
        buggs_obj = buggs()
        buggs_obj.app_version_code=int(self.request.get('APP_VERSION_CODE'))
        buggs_obj.phone_model=self.request.get('PHONE_MODEL')
        buggs_obj.brand=self.request.get('BRAND')
        buggs_obj.android_version=self.request.get('ANDROID_VERSION')
        buggs_obj.build=self.request.get('BUILD')
        buggs_obj.total_mem_size=self.request.get('TOTAL_MEM_SIZE')
        buggs_obj.available_mem_size=self.request.get('AVAILABLE_MEM_SIZE')
        buggs_obj.a_stack_trace=self.request.get('STACK_TRACE')
        buggs_obj.crash_conf=self.request.get('CRASH_CONFIGURATION')
        crash_date = self.request.get('USER_CRASH_DATE')
        buggs_obj.user_crash_date=crash_date[:19]
        buggs_obj.put() 

application = webapp2.WSGIApplication([('/',buggs_insert)], debug=True)
