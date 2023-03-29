package application.model;

import java.util.List;

public interface ConductorDBManager {

    Conductor getDetails();
    List<Ticket> viewScanHistory();
    boolean scanTicket(String busRegistrationNumber,String ticketID);

}
