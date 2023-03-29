package application.model;

import application.Enum.Gender;
import application.model.Tickets.Ticket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client extends User{

    private long bankAccountNumber = -1;
    private String eWalletID = null;

    public String currentLocation;

    public final Map<String, Ticket> ticketHistory;
    public final Map<String,Ticket> validTickets;
    public final Map<String,Ticket> canceledTickets;

    public final Map<String, Plan> planHistory;
    public final Map<String, Plan> validPlans;

    public final Map<String,List<Path>> favouriteRoutes;
    public final Map<String,Passenger> passengerList;
    //TODO add news and notification



    public Client(String name, String emailID, String DOB, Gender gender) {
        super(name,emailID,DOB,gender);

        ticketHistory = new HashMap<>();
        validTickets = new HashMap<>();
        favouriteRoutes = new HashMap<>();
        planHistory = new HashMap<>();
        validPlans = new HashMap<>();
        passengerList = new HashMap<>();
        canceledTickets = new HashMap<>();
    }

    public Client(Client clientUser){
        super(clientUser.name, clientUser.EMAIL_ID, clientUser.DOB, clientUser.GENDER);
        ticketHistory = new HashMap<>(clientUser.ticketHistory);
        validTickets = new HashMap<>(clientUser.validTickets);
        canceledTickets = new HashMap<>(clientUser.canceledTickets);

        planHistory = new HashMap<>(clientUser.planHistory);
        validPlans = new HashMap<>(clientUser.validPlans);

        favouriteRoutes = new HashMap<>(clientUser.favouriteRoutes);
        passengerList = new HashMap<>(clientUser.passengerList);

        this.setEWalletID(clientUser.eWalletID);
        this.setBankAccountNumber(clientUser.bankAccountNumber);
        this.currentLocation = clientUser.currentLocation;
    }

    public long getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(long bankAccountNumber) {
        if(this.bankAccountNumber == -1)
            this.bankAccountNumber = bankAccountNumber;
    }

    public String getEWalletID() {
        return eWalletID;
    }

    public void setEWalletID(String eWalletID) {
        if(this.eWalletID == null)
            this.eWalletID = eWalletID;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }
}
