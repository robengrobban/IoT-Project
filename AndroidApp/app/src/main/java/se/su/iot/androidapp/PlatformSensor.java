package se.su.iot.androidapp;

import java.io.Serializable;

public class PlatformSensor implements Serializable {

    private String uuid;
    private double position;

    public PlatformSensor(String uuid, double position) {
        this.uuid = uuid;
        this.position = position;
    }

    public String getUuid() {
        return uuid;
    }

    public double getPosition() {
        return position;
    }

    @Override
    public boolean equals(Object other) {
        if ( other instanceof PlatformSensor ) {
            PlatformSensor sensor = (PlatformSensor) other;
            return this.uuid.equals(sensor.uuid);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

}
