package se.su.iot.androidapp;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

public class Train {

    private final int id;

    private final SortedSet<Carriage> carriages;

    public Train(int id) {
        this.id = id;
        this.carriages = new TreeSet<>();
    }

    public int getId() {
        return this.id;
    }

    public void addCarriage(Carriage carriage) {
        if ( carriages.contains(carriage) ) {
            return;
        }
        carriages.add(carriage);
    }

    public void removeCarriage(Carriage carriage) {
        carriages.remove(carriage);
    }

    public boolean containsCarriage(Carriage carriage) {
        return carriages.contains(carriage);
    }

    public SortedSet<Carriage> getCarriages() {
        return Collections.unmodifiableSortedSet(carriages);
    }

}
