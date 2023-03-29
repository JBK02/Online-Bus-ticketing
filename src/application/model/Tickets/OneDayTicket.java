package application.model.Tickets;

import application.model.Passenger;

import java.time.LocalDateTime;
import java.util.List;

public class OneDayTicket extends Ticket{

    public OneDayTicket(String ID, List<Passenger> passengerList, LocalDateTime timeStamp, double cost) {
        super(ID, passengerList, timeStamp, null, cost);
    }
}
