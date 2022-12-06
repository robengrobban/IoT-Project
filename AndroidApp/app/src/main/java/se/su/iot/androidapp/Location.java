package se.su.iot.androidapp;

import java.util.*;

public class Location {

    private final Platform platform;
    private final Map<String, Sensor> sensorsByUuid;
    private final List<Sensor> sensorsInOrder;

    private double relativePosition;

    public Location(Platform platform) {
        this.platform = platform;

        this.sensorsByUuid = new HashMap<>();
        this.sensorsInOrder = new ArrayList<>(platform.getSensors().size());

        for ( Sensor sensor : platform.getSensors() ) {
            sensorsByUuid.put(sensor.getUuid(), sensor);
            sensorsInOrder.add(sensor);
        }
        sensorsInOrder.sort((first, second) -> {
            return (int) (first.getRelativePosition()*100 - second.getRelativePosition()*100);
        });

    }

    public void calculateRelativePosition() {

    }

    public void updateDistances(String uuid, double distance) {
        sensorsByUuid.get(uuid).setDistance(distance);
    }

}
