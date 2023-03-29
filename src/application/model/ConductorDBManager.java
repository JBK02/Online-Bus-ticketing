package application.model;

import application.model.Tickets.Ticket;

import java.util.List;

public interface ConductorDBManager {

    Conductor getDetails();

    List<Ticket> viewScanHistory();

    boolean scanTicket(String busRegistrationNumber,String ticketID);

}