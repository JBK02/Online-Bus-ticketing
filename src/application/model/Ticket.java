package application.model;

import application.model.Path;
import application.model.Passenger;

import java.time.LocalDateTime;
import java.util.List;

public class Ticket {

    public final String ID;
    public final List<Passenger> passengerList;

    public final LocalDateTime purchaseTimeStamp;
    public List<Path> pathList;
    public final double cost;

    private boolean validity;

    public Ticket(String ID, List<Passenger> passengerList, LocalDateTime timeStamp,List<Path> pathList, double cost) {
        this.ID = ID;
        this.passengerList = passengerList;
        this.purchaseTimeStamp = timeStamp;
        this.cost = cost;
        this.pathList = pathList;
        this.validity = true;
    }


    public boolean isValidity() {
        return validity;
    }

    public void changeValidity() {
        this.validity = false;
    }

}
