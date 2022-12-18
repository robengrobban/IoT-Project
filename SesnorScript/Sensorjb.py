import time
import json
import board
import adafruit_vcnl4010
import adafruit_tca9548a
import paho.mqtt.client as mqtt
import paho.mqtt.publish as publish


broker = "test.mosquitto.org"                               # Test Broker.
#broker = "tcp://83.226.147.68:1883"                        # Broker IP, used when publishing sensory data
topic = "carriage/id"                                       # Doublecheck this value


i2c= board.I2C()                                            # Init board
#sensor_prox = adafruit_vcnl4010.VCNL4010(i2c)	            # Init proximity sensor. Needed?


############### Multiplexer section ##################
sensorlist=list()
tca = adafruit_tca9548a.TCA9548A(i2c)                                       # Init multiplexer
for channel in range(8):                                                    # Scan the multiplexer for sensors with addresses. Copied form tutorial
    if tca[channel].try_lock():                                             # Channels are numbered 0-7
        print("Channel {}:".format(channel), end="")
        addresses = tca[channel].scan()
        print([hex(address) for address in addresses if address != 0x70])
        for address in addresses:                                           #Stores all detected values (except 112/0x70) in a separate list, sensorlist
            if address !=0x70:
                sensorlist.append(address)
        #print(sensorlist)
        tca[channel].unlock()

totalSeats= len(sensorlist)
print("Total no. of detected seats/sensors: ", totalSeats)    

        
############### Sensor section ##################

sensor_prox_first = adafruit_vcnl4010.VCNL4010(tca[1])      # First sensor. Hardcoded to channel 1 from previous channel scan
sensor_prox_second = adafruit_vcnl4010.VCNL4010(tca[6])     # Second sensor. Hardcoded to channel 6 from previous channel scan

def get_proximity(sensor):                                  # Create a method for getting proximity data from a sensor.
	proximity = sensor.proximity
	print('Proximity: {0}'.format(proximity))
	return proximity


############### MQTT section ################## (from lab)

# when connecting to mqtt do this;
def on_connect(client, userdata, flags, rc):
	if rc==0:
		print("Connection established. Code: "+str(rc))
	else:
		print("Connection failed. Code: " + str(rc))
		
def on_publish(client, userdata, mid):
    print("Published: " + str(mid))
	
def on_disconnect(client, userdata, rc):
	if rc != 0:
		print ("Unexpected disonnection. Code: ", str(rc))
	else:
		print("Disconnected. Code: " + str(rc))
	
def on_log(client, userdata, level, buf):		                # Message is in buf
    print("MQTT Log: " + str(buf))

# Connect functions for MQTT
client = mqtt.Client()
client.on_connect = on_connect
client.on_disconnect = on_disconnect
client.on_publish = on_publish
client.on_log = on_log

#Connect to MQTT 
print("Attempting to connect to broker " + broker)
client.connect(broker)	                                       # Broker address, port and keepalive (maximum period in seconds allowed between communications with the broker)
client.loop_start()

############### Data processing and publishing ################## 

occupiedSeats=int                                             # Will be calculated from senory data
availableSeats=int                                            # will be derived later
sensordata_list = list()                                       #
for each_seat in range (totalSeats):                          # Create a list with length totalSeats  with "NoN" as dummy value fpr each index
    sensordata_list.append("NoN")                             # maybe skip this loop and just append?
    print(sensordata_list)
print("initialized list:",sensordata_list)


while True: # Loop and read proximity from sensor_prox_first and sensor_prox_second
   
    prox_first = get_proximity(sensor_prox_first)
    prox_second  = get_proximity(sensor_prox_second)

    if prox_first <=2600:                                   # Find a better way to loop over sensors and populate the list?
        sensordata_list[0] = False                          # Populates a list with 2 elements (index 0 is for first sensor and index 1 is for second sensor) 
    else: sensordata_list[0]=True                           # Values are either False or True and I have used arbitrarily chosen values as conditions.
                                                            # True if sensor value is greater than 2600, meaning the seat is occupied.
    if prox_second <=2600:                                  # False if the sensor value is eqyualt to or less than 2600, menaning the seat is not occupied.
        sensordata_list[1]=False
    else: sensordata_list[1]=True
    
    occupiedSeats = sensordata_list.count(True)
    availableSeats = totalSeats - occupiedSeats
    
    carriage_status = {                                      # Create a dict to contain values
        "id": "1",                                           # Carriage ID. Hardcoded value
        "occupiedSeats": occupiedSeats,     
        "availableSeats": availableSeats
    }
    carriage_json = json.dumps(carriage_status)             # Convert dict to json string
    payload=carriage_json
    client.publish(topic, str(payload))                     # Publish
    print(payload)
    time.sleep(1.0)