package application.model.Tickets;

import application.model.Passenger;
import application.model.Path;

import java.time.LocalDateTime;
import java.util.List;

public class OneWayTicket extends Ticket{

    public OneWayTicket(String ID, List<Passenger> passengerList, LocalDateTime timeStamp, List<Path> pathList, double cost) {
        super(ID, passengerList, timeStamp, pathList, cost);
    }
}
