package application.model.Tickets;

import application.model.Passenger;
import application.model.Path;

import java.time.LocalDateTime;
import java.util.List;

public abstract class Ticket {

    public final String ID;
    public final List<Passenger> passengerList;

    public final LocalDateTime purchaseTimeStamp;
    public final List<Path> pathList;
    public final double cost;

    private boolean validity;

    Ticket(String ID, List<Passenger> passengerList, LocalDateTime timeStamp,List<Path> pathList, double cost) {
        this.ID = ID;
        this.passengerList = passengerList;
        this.purchaseTimeStamp = timeStamp;
        this.cost = cost;
        this.pathList = pathList;
        this.validity = true;
    }

/*  One Day ticket constructor
    Ticket(String ID, List<Passenger> passengerList, LocalDateTime timeStamp, double cost) {
        this.ID = ID;
        this.passengerList = passengerList;
        this.purchaseTimeStamp = timeStamp;
        this.cost = cost;
        this.validity = true;

        this.pathList = null;
    }
*/

    public boolean isValidity() {
        return validity;
    }

    public void changeValidity() {
        this.validity = false;
    }

}
