import webapp2
from google.appengine.ext import ndb

class crkmatch(ndb.Model):
    device_id = ndb.IntegerProperty(indexed=True)
    duration = ndb.StringProperty(indexed=False)
    first_action = ndb.StringProperty(indexed=False)
    innings = ndb.IntegerProperty(indexed=False)
    level = ndb.StringProperty(indexed=False)
    match_date = ndb.StringProperty(indexed=False)
    match_id = ndb.IntegerProperty(indexed=True)
    my_team = ndb.StringProperty(indexed=False)
    opponent_team = ndb.StringProperty(indexed=False)
    overs = ndb.IntegerProperty(indexed=False)
    result = ndb.StringProperty(indexed=False)
    review = ndb.StringProperty(indexed=False)
    status = ndb.IntegerProperty(indexed=False)
    user_id = ndb.StringProperty(indexed=True)
    venue = ndb.StringProperty(indexed=False)

class crkmatch_insert(webapp2.RequestHandler):

    def post(self):

        self.response.headers['Content-Type'] = 'text/plain'

        crkmatch_obj = crkmatch()
        crkmatch_obj.device_id = int(self.request.get('device_id'))
        crkmatch_obj.duration = self.request.get('duration')
        crkmatch_obj.first_action = self.request.get('first_action')
        crkmatch_obj.innings = int(self.request.get('innings'))
        crkmatch_obj.level = self.request.get('level')
        crkmatch_obj.match_date = self.request.get('match_date')
        crkmatch_obj.match_id = int(self.request.get('match_id'))
        crkmatch_obj.my_team = self.request.get('my_team')
        crkmatch_obj.opponent_team = self.request.get('opponent_team')
        crkmatch_obj.overs = int(self.request.get('overs'))
        crkmatch_obj.result = self.request.get('result')
        crkmatch_obj.review = self.request.get('review')
        crkmatch_obj.status = int(self.request.get('status'))
        crkmatch_obj.user_id = self.request.get('user_id')
        crkmatch_obj.venue = self.request.get('venue')

        obj_list = crkmatch.query(
        ndb.AND(
        crkmatch.user_id == crkmatch_obj.user_id,
        crkmatch.match_id ==crkmatch_obj.match_id,
        crkmatch.device_id ==crkmatch_obj.device_id
        )).fetch()
        if(len(obj_list) == 0):
            crkmatch_obj.put()
            self.response.write('1 row inserted')
        else:
            self.response.write('row already exists')

application = webapp2.WSGIApplication([
    ('/', crkmatch_insert),
], debug=True)
