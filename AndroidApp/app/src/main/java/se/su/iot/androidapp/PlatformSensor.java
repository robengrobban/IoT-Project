package se.su.iot.androidapp;

import java.io.Serializable;

public class PlatformSensor implements Serializable {

    private String uuid;
    private String platform;
    private double position;

    public PlatformSensor(String uuid, String platform, double position) {
        this.uuid = uuid;
        this.platform = platform;
        this.position = position;
    }

    public String getUuid() {
        return uuid;
    }

    public String getPlatform() {
        return platform;
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
