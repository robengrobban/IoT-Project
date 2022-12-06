package se.su.iot.androidapp;

import java.io.Serializable;

public class Sensor implements Serializable {

    private final String uuid;
    private final double relativePosition;
    private double distance;

    public Sensor(String uuid, double position) {
        this.uuid = uuid;
        this.relativePosition = position;
    }

    public String getUuid() {
        return uuid;
    }

    public double getRelativePosition() {
        return relativePosition;
    }

    @Override
    public boolean equals(Object other) {
        if ( other instanceof Sensor) {
            Sensor sensor = (Sensor) other;
            return this.uuid.equalsIgnoreCase(sensor.uuid);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

}
