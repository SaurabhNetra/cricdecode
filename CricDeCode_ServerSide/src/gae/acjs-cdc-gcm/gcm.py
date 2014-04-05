import webapp2
import urllib
import urllib2
import json

class send_gcm(webapp2.RequestHandler):
    def post(self):
        self.response.headers['Content-Type'] = 'text/plain'       
        uid = self.request.get('uid')
        msgToSend = self.request.get('MsgToSend')
        sendTo = str(self.request.get('SendToArray')).split()
        json_data = {"registration_ids" : sendTo, "data" : { "cricdecode" : msgToSend }}
        data = json.dumps(json_data) 
        headers = {'Content-Type': 'application/json', 'Authorization': 'key=AIzaSyAF4WTV0hbl0eG_GdTYjvnxaZ9C4CGdnOM'}
        req = urllib2.Request('https://android.googleapis.com/gcm/send', data, headers)
        f = urllib2.urlopen(req)
        try:
            i=-1
            res = json.loads(f.read())         
            for gcm_res in res['results']:
                i = i + 1
                try:
                    if gcm_res['error'] == "NotRegistered":
                        values = {'gcm_id' : sendTo[i],'user_id' : uid}
                        urllib2.urlopen(urllib2.Request("http://acjs-cdc-andro.appspot.com/delete", urllib.urlencode(values))).read()
                except:
                    try:
                        values = {'old_reg' : sendTo[i], 'new_reg': gcm_res['registration_id'], 'user_id' : uid}
                        urllib2.urlopen(urllib2.Request("http://acjs-cdc-andro.appspot.com/update", urllib.urlencode(values))).read()
                    except:
                        j=False
        except:
            j=False
        self.response.write('{"status" : 1}')
application = webapp2.WSGIApplication([('/sendgcm', send_gcm)], debug=True)
