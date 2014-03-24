import webapp2
import json
from google.appengine.ext import ndb

class per(ndb.Model):
    bat_balls = ndb.IntegerProperty(indexed=False)
    bat_bowler_type = ndb.StringProperty(indexed=False)
    bat_chances = ndb.IntegerProperty(indexed=False)
    bat_dismissal = ndb.StringProperty(indexed=False)
    bat_fielding_position = ndb.StringProperty(indexed=False)
    bat_num = ndb.IntegerProperty(indexed=False)
    bat_runs = ndb.IntegerProperty(indexed=False)
    bat_time = ndb.IntegerProperty(indexed=False)
    bat_fours = ndb.IntegerProperty(indexed=False)
    bat_sixes = ndb.IntegerProperty(indexed=False)

    bowl_balls = ndb.IntegerProperty(indexed=False)
    bowl_catches_dropped = ndb.IntegerProperty(indexed=False)
    bowl_fours = ndb.IntegerProperty(indexed=False)
    bowl_maidens = ndb.IntegerProperty(indexed=False)
    bowl_no_balls = ndb.IntegerProperty(indexed=False)
    bowl_runs = ndb.IntegerProperty(indexed=False)
    bowl_sixes = ndb.IntegerProperty(indexed=False)
    bowl_spells = ndb.IntegerProperty(indexed=False)
    bowl_wides = ndb.IntegerProperty(indexed=False)
    bowl_wkts_left = ndb.IntegerProperty(indexed=False)
    bowl_wkts_right = ndb.IntegerProperty(indexed=False)

    device_id = ndb.IntegerProperty(indexed=True)

    field_byes = ndb.IntegerProperty(indexed=False)
    field_catches_dropped = ndb.IntegerProperty(indexed=False)
    field_circle_catch = ndb.IntegerProperty(indexed=False)
    field_close_catch = ndb.IntegerProperty(indexed=False)
    field_deep_catch = ndb.IntegerProperty(indexed=False)
    field_misfield = ndb.IntegerProperty(indexed=False)
    field_ro_circle = ndb.IntegerProperty(indexed=False)
    field_ro_deep = ndb.IntegerProperty(indexed=False)
    field_ro_direct_circle = ndb.IntegerProperty(indexed=False)
    field_ro_direct_deep = ndb.IntegerProperty(indexed=False)
    field_slip_catch = ndb.IntegerProperty(indexed=False)
    field_stumpings = ndb.IntegerProperty(indexed=False)

    inning = ndb.IntegerProperty(indexed=True)
    match_id = ndb.IntegerProperty(indexed=True)
    per_id = ndb.IntegerProperty(indexed=False)
    status = ndb.IntegerProperty(indexed=True)
    user_id = ndb.StringProperty(indexed=True)

class per_insert(webapp2.RequestHandler):

    def post(self):

        self.response.headers['Content-Type'] = 'text/plain'

        per_json = json.loads(self.request.get('perData'))
        per_array = per_json["per"]

        for per_obj in per_array:
            perf_obj = per()

            perf_obj.bat_balls = per_obj['bat_balls']
            perf_obj.bat_bowler_type = per_obj['bat_bowler_type']
            perf_obj.bat_chances = per_obj['bat_chances']
            perf_obj.bat_dismissal = per_obj['bat_dismissal']
            perf_obj.bat_fielding_position = per_obj['bat_fielding_position']
            perf_obj.bat_num = per_obj['bat_num']
            perf_obj.bat_runs = per_obj['bat_runs']
            perf_obj.bat_time = per_obj['bat_time']
            perf_obj.bat_fours = per_obj['bat_fours']
            perf_obj.bat_sixes = per_obj['bat_sixes']

            perf_obj.bowl_balls = per_obj['bowl_balls']
            perf_obj.bowl_catches_dropped = per_obj['bowl_catches_dropped']
            perf_obj.bowl_fours = per_obj['bowl_fours']
            perf_obj.bowl_maidens = per_obj['bowl_maidens']
            perf_obj.bowl_no_balls = per_obj['bowl_no_balls']
            perf_obj.bowl_runs = per_obj['bowl_runs']
            perf_obj.bowl_sixes = per_obj['bowl_sixes']
            perf_obj.bowl_spells = per_obj['bowl_spells']
            perf_obj.bowl_wides = per_obj['bowl_wides']
            perf_obj.bowl_wkts_left = per_obj['bowl_wkts_left']
            perf_obj.bowl_wkts_right = per_obj['bowl_wkts_right']

            perf_obj.device_id = per_obj['device_id']

            perf_obj.field_byes = per_obj['field_byes']
            perf_obj.field_catches_dropped = per_obj['field_catches_dropped']
            perf_obj.field_circle_catch = per_obj['field_circle_catch']
            perf_obj.field_close_catch = per_obj['field_close_catch']
            perf_obj.field_deep_catch = per_obj['field_deep_catch']
            perf_obj.field_misfield = per_obj['field_misfield']
            perf_obj.field_ro_circle = per_obj['field_ro_circle']
            perf_obj.field_ro_deep = per_obj['field_ro_deep']
            perf_obj.field_ro_direct_circle = per_obj['field_ro_direct_circle']
            perf_obj.field_ro_direct_deep = per_obj['field_ro_direct_deep']
            perf_obj.field_slip_catch = per_obj['field_slip_catch']
            perf_obj.field_stumpings = per_obj['field_stumpings']
            perf_obj.inning = per_obj['inning']
            perf_obj.match_id = per_obj['match_id']
            perf_obj.per_id = per_obj['per_id']
            perf_obj.status = per_obj['status']
            perf_obj.user_id = per_obj['user_id']

            obj_list = serverdbperformance.query(
            ndb.AND(
            per.user_id == perf_obj.user_id,
            per.match_id == perf_obj.match_id,
            per.device_id == perf_obj.device_id,
            per.inning == perf_obj.inning
            )).fetch()
            if(len(obj_list) == 0):
                perf_obj.put()

        per_response = {}
        per_response["status"] = 1
        self.response.write(json.dumps(per_response))


class per_fetch(webapp2.RequestHandler):

     def post(self):
        self.response.headers['Content-Type'] = 'text/plain'

        user_id = self.request.get("user_id")
        obj_list = per.query(ndb.AND(
        per.user_id == user_id,
        per.status == 0
        )).fetch()

        json_obj = {}
        per_array = []
        for obj in obj_list:
            per_obj = {}

            per_obj["bat_balls"] = obj.bat_balls
            per_obj["bat_bowler_type"] = obj.bat_bowler_type
            per_obj["bat_chances"] = obj.bat_chances
            per_obj["bat_dismissal"] = obj.bat_dismissal
            per_obj["bat_fielding_position"] = obj.bat_fielding_position
            per_obj["bat_num"] = obj.bat_num
            per_obj["bat_runs"] = obj.bat_runs
            per_obj["bat_time"] = obj.bat_time
            per_obj["bat_fours"] = obj.bat_fours
            per_obj["bat_sixes"] = obj.bat_sixes

            per_obj["bowl_balls"] = obj.bowl_balls
            per_obj["bowl_catches_dropped"] = obj.bowl_catches_dropped
            per_obj["bowl_fours"] = obj.bowl_fours
            per_obj["bowl_maidens"] = obj.bowl_maidens
            per_obj["bowl_no_balls"] = obj.bowl_no_balls
            per_obj["bowl_runs"] = obj.bowl_runs
            per_obj["bowl_sixes"] = obj.bowl_sixes
            per_obj["bowl_spells"] = obj.bowl_spells
            per_obj["bowl_wides"] = obj.bowl_wides
            per_obj["bowl_wkts_left"] = obj.bowl_wkts_left
            per_obj["bowl_wkts_right"] = obj.bowl_wkts_right

            per_obj["field_byes"] = obj.field_byes
            per_obj["field_catches_dropped"] = obj.field_catches_dropped
            per_obj["field_circle_catch"] = obj.field_circle_catch
            per_obj["field_close_catch"] = obj.field_close_catch
            per_obj["field_deep_catch"] = obj.field_deep_catch
            per_obj["field_misfield"] = obj.field_misfield
            per_obj["field_ro_circle"] = obj.field_ro_circle
            per_obj["field_ro_deep"] = obj.field_ro_deep
            per_obj["field_ro_direct_circle"] = obj.field_ro_direct_circle
            per_obj["field_ro_direct_deep"] = obj.field_ro_direct_deep
            per_obj["field_slip_catch"] = obj.field_slip_catch
            per_obj["field_stumpings"] = obj.field_stumpings

            per_obj["inning"] = obj.inning
            per_obj["match_id"] = obj.match_id
            per_obj["per_id"] = obj.per_id
            per_obj["status"] = obj.status
            per_obj["user_id"] = obj.user_id

            per_array.append(per_obj)


        json_obj["performances"] = per_array
        self.response.write(json.dumps(json_obj))


application = webapp2.WSGIApplication([
    ('/insert', per_insert),
    ('/fetch', per_fetch),
], debug=True)