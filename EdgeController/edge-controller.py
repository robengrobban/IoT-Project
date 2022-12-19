#The edge controller script will have 3 components inside of it.
#One datastructure that holds information about all the availabe carriages.
# - Each carriage that is stored will have information about its ID, its position, what train it belongs to and what the crowdedness is.
#It will have a subscriber part
# - It will subscribe to topics for each carriage. For instance, if there are 2 carraiges with id 1 and 2. The script will subscribe to
#   carriage/1 and carriage/2. Information will probably be sent in a JSON format. The infromation that will be sent is about the carriages
#   seats. Such as there are 2 seats in total and 1 seat is occupied. That information can be stored in the datastructure, the first component.
#It will have a publisher part
# - For each train that the edge controller knows exists (by looking at its carriages and what train they exists too). The edge controler will
#   publish information about the trains carriages, and their crowdedness. The format that will be published is as a JSON object, an example is
#   written out down there. 
#Down in the documnet, there are more comments, basically the same as up here, but maybe more in a step by step maner.
#There is a HTTP GET call down there as well. It is used to get information about what carriages exists. So that the script can build a
#datastrcuture to store carriage and crowdedness information.

import time
import datetime
import json
import requests
import paho.mqtt.client as mqtt
import paho.mqtt.publish as publish
import paho.mqtt.subscribe as subscribe

broker = "83.226.147.68"
#broker = "tcp://83.226.147.68:1883"

#HTTP Request to http://iot.studentenfix.se/carriage/ for information about all the carraiges.
#The information is acquired as a JSON format. It holds information about all the carriages that 
#exists (their id and position and what train they belong too)
#This information can be stored in a datastrcuture, list and or maps, so that the edge controller can
#keep track of each carriage, what train they belong too, their train position and their crowdedness.
carriageData = requests.get(url="http://iot.studentenfix.se/carriage/").json()

print(json.dumps(carriageData, indent=2))

trains = dict()
carriages = dict()

for carriage in carriageData:
    print(carriage)
    
    trainId = carriage['train_id']
    if trainId not in trains:
        trains[trainId] = {
            'id': trainId,
            'carriages': []
        }
    
    trains[trainId]['carriages'].append(carriage['id'])

    carriageId = carriage['id']
    carriages[carriageId] = {
        'id': carriageId,
        'position': carriage['position'],
        'train_id': trainId,
        'crowdedness': 0.0
    }

def getTrain(id):
    train = trains[id]
    carriagesId = train['carriages']
    print(carriagesId)

    train['carriages'] = []

    for i in carriagesId:
        train['carriages'].append(carriages[i])

    return train

def updateCarriage(id, crowdedness):
    carriages[id]['crowdedness'] = crowdedness

updateCarriage(1, 0.5)
print(getTrain(1))

#Subscribe to topics related to each carriage
#Each carriage will publish data on carriate/{id} <-- with their id. The edge controller will gather that data (we are not sure how it will look right
#now, but it will probably be as a JSON structure, containing information about available seats and occupied seats).
#This information can be saved in a datastructure, so that the edge controller has information about each carriage and their crowdedness. Crowdedness can
#be calculated as a % of available seats.

#creating a mqtt client instance
clinet= mqtt.Client()

#connecting to the mqtt broker
clinet.connect(broker)

#subscribing to the topic 
for id in carriages:
    print("Subscribing to: " + "carriage/"+str(id))
    clinet.subscribe("carriage/"+str(id), qos= 0)

def on_connect(client, uderdata, flags, rc):
    print("Connected " + str(rc))

def on_message(client, userdata, message):
    data= (str(msg.payload.decode("utf-8")))
    json_object= json.loads(data)
    print(json_object)
    print(json_object['id'], json_object['availableSeats'], json_object['occupiedSeats'])

    #printing the message payload
    print(message.payload)
    
    client.on_connect = on_connect

    #setting on message callback
    client.on_message = on_message

    #looping to forever receive messages
    client.loop_forever()





#Aggregate the carriage data
#This point is just to emphasize that the carriage data should be saved in a datastructure, list and or maps. The crowdedness data must be updated
#when it gets information from the subscribed topic.



#Publish train crowdedness infromation
#In order for the Phone to get information about the next trains crowdedness, they will subscribe to topics in the form of train/{id} <-- where the ID is
#the id for the train. The edge controller will need to every 10s or so look in its datastrcuture that stores information about its carriages and publish
#data onto each train that is stores information about. Down here is the format for the published data, which will be as a JSON object.
#Publish format
#topic: train/{id} --> trian/1 in this case

#publsihing the information

clinet.publish("id", "train" qos= 0)

clinet.subscribe("crowdedness", qos= 0)

topic= message.topic
m_decode= str(message.payload.decode("utf-8", "ignore"))
print("data received type", type(m_decode))
print("data received", m_decode)
print("converting from json to object")
m_in=json.loads(m_decode) #decode json data
print(type(m_in))
print("broker address = ",m_in["broker"])

#Using getTrain(id) would result in a python dictonary with this information
#{
#	"train":{"id":1},
#	"carriages":[
#		{"id":1,"position":1,"train_id":1,"crowdedness":0.10},
#		{"id":2,"position":2,"train_id":1,"crowdedness":0.60}
#	]
#}


