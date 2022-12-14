import time

import board
import adafruit_vcnl4010
import adafruit_tca9548a

i2c = board.I2C() #Create an instance to the board 
tca = adafruit_tca9548a.TCA9548A(i2c) #Create an instance to the multiplexor

for channel in range(8): #This is just to scan the multiplexor for sensors with addresses. Copied form tutorial
    if tca[channel].try_lock():
        print("Channel {}:".format(channel), end="")
        addresses = tca[channel].scan()
        print([hex(address) for address in addresses if address != 0x70])
        tca[channel].unlock()

sensor_prox_first = adafruit_vcnl4010.VCNL4010(tca[1]) #Hardcoded, I checked and saw that the first sensor is on channel 1
sensor_prox_second = adafruit_vcnl4010.VCNL4010(tca[6]) #Hardcoded, I checked and saw that the second sensor is on channel 2 (6?)

def get_proximity(sensor): #Create a method for getting proximity data from a sensor.
	proximity = sensor.proximity
	print('Proximity: {0}'.format(proximity))
	return proximity

while True: # Loop and read proximity from sensor one and sensor two
    get_proximity(sensor_prox_first)-
    get_proximity(sensor_prox_second)
    time.sleep(1.0)
