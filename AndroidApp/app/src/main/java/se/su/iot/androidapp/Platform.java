package se.su.iot.androidapp;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.*;

public class Platform implements Serializable {

    private final String name;
    private final Set<Sensor> sensors;

    public Platform(String name) {
        this(name, new HashSet<>());
    }
    public Platform(String name, Set<Sensor> sensors) {
        this.name = name;
        this.sensors = sensors;
    }

    public void addSensor(Sensor sensor) {
        sensors.add(sensor);
    }

    public String getName() {
        return name;
    }
    public Set<Sensor> getSensors() {
        return Collections.unmodifiableSet(sensors);
    }

}
