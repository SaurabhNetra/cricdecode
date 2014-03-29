import webapp2
import urllib
import json
from google.appengine.api import urlfetch

class send_gcm(webapp2.RequestHandler):
    def post(self):
        self.response.headers['Content-Type'] = 'text/plain'       
        uid = self.request.get('uid')
        msgToSend = self.request.get('MsgToSend')
        sendToString = str(self.request.get('SendToArray'))
        sendTo = sendToString.split(' ')
        self.response.write(sendTo)
        self.response.write(msgToSend)
        msg = {}
        msg['registration_ids'] = sendTo
        msg['data'] = {}
        msg['data']['cricdecode'] = json.loads(msgToSend)
        self.response.write(msg)
        url = 'https://android.googleapis.com/gcm/send'
        headers ={'Authorization':'key=AIzaSyAF4WTV0hbl0eG_GdTYjvnxaZ9C4CGdnOM ','Content-Type': 'application/x-www-form-urlencoded'}
        form_data = urllib.urlencode(msg)
        r = urlfetch.fetch(url=url, payload=form_data, method=urlfetch.POST, headers=headers)
        self.response.write(r.content)
application = webapp2.WSGIApplication([('/sendgcm', send_gcm)], debug=True)
