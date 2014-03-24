import webapp2
import json
import urllib
import urllib2
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
    status = ndb.IntegerProperty(indexed=True)
    user_id = ndb.StringProperty(indexed=True)
    venue = ndb.StringProperty(indexed=False)

class crkmatch_insert(webapp2.RequestHandler):

    def post(self):

        self.response.headers['Content-Type'] = 'text/plain'

        match_json = json.loads(self.request.get('matchData'))

        crkmatch_obj = crkmatch()
        crkmatch_obj.device_id = match_json['device_id']
        crkmatch_obj.duration = match_json['duration']
        crkmatch_obj.first_action = match_json['first_action']
        crkmatch_obj.innings = match_json['innings']
        crkmatch_obj.level = match_json['level']
        crkmatch_obj.match_date = match_json['match_date']
        crkmatch_obj.match_id = match_json['match_id']
        crkmatch_obj.my_team = match_json['my_team']
        crkmatch_obj.opponent_team = match_json['opponent_team']
        crkmatch_obj.overs = match_json['overs']
        crkmatch_obj.result = match_json['result']
        crkmatch_obj.review = match_json['review']
        crkmatch_obj.status = match_json['status']
        crkmatch_obj.user_id = match_json['user_id']
        crkmatch_obj.venue = match_json['venue']

        obj_list = crkmatch.query(
        ndb.AND(
        crkmatch.user_id == crkmatch_obj.user_id,
        crkmatch.match_id ==crkmatch_obj.match_id,
        crkmatch.device_id ==crkmatch_obj.device_id
        )).fetch()

        if(len(obj_list) == 0):

            crkmatch_obj.put()

            url = "http://acjs-cdc-per.appspot.com/insert"
            values = {}
            values['perData'] = self.request.get('perData')
            data = urllib.urlencode(values)
            req = urllib2.Request(url, data)
            response = urllib2.urlopen(req)
            per_response = json.loads(response.read())

        per_response = {}
        per_response["status"] = 1
        self.response.write(json.dumps(per_response))

class crkmatch_fetch(webapp2.RequestHandler):

     def post(self):
        self.response.headers['Content-Type'] = 'text/plain'

        user_id = self.request.get("user_id")
        obj_list = crkmatch.query(ndb.AND(
        crkmatch.user_id == user_id,
        crkmatch.status == 0
        )).fetch()

        json_obj = {}
        match_array = []
        for obj in obj_list:
            match_obj = {}
            match_obj["device_id"] = obj.device_id
            match_obj["duration"] = obj.duration
            match_obj["first_action"] = obj.first_action
            match_obj["innings"] = obj.innings
            match_obj["level"] = obj.level
            match_obj["match_date"] = obj.match_date
            match_obj["match_id"] = obj.match_id
            match_obj["my_team"] = obj.my_team
            match_obj["opponent_team"] = obj.opponent_team
            match_obj["overs"] = obj.overs
            match_obj["result"] = obj.result
            match_obj["review"] = obj.review
            match_obj["status"] = obj.status
            match_obj["user_id"] = obj.user_id
            match_obj["venue"] = obj.venue
            match_array.append(match_obj)
        json_obj["matches"] = match_array
        self.response.write(json.dumps(json_obj))


application = webapp2.WSGIApplication([
    ('/insert', crkmatch_insert),
    ('/fetch', crkmatch_fetch),
], debug=True)