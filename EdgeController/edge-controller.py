import time
import datetime
import json
import requests
import paho.mqtt.client as mqtt
import paho.mqtt.publish as publish
import paho.mqtt.subscribe as subscribe

#Publish format
#topic: train/{id} --> trian/1 in this case
#{
#	"train":{"id":1},
#	"carriages":[
#		{"id":1,"position":1,"train_id":1,"crowdedness":0.10},
#		{"id":2,"position":2,"train_id":1,"crowdedness":0.60}
#	]
#}

#HTTP Request to http://iot.studentenfix.se/carriage/ for information about all the carraiges.


#Subscribe to topics related to each carriage

#Aggregate the carriage data

#Publish train crowdedness infromation
