package se.su.iot.androidapp;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Platform implements Serializable {

    private String name;
    private Set<PlatformSensor> sensors;

    public Platform(String name) {
        this(name, new HashSet<>());
    }
    public Platform(String name, Set<PlatformSensor> sensors) {
        this.name = name;
        this.sensors = sensors;
    }

    public void addSensor(PlatformSensor sensor) {
        sensors.add(sensor);
    }

    public String getName() {
        return name;
    }
    public Set<PlatformSensor> getSensors() {
        return Collections.unmodifiableSet(sensors);
    }

}
