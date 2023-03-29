package application.model;

import application.Enum.Gender;
import application.model.Tickets.Ticket;

import java.util.ArrayList;
import java.util.List;

public class Conductor extends User {

    public final List<Ticket> scanedTickectList;
    private String allottedBus;

    public Conductor(String name, String EMAIL_ID, String DOB, Gender GENDER, String allottedBus, List<Ticket> scanedTickectList) {
        super(name, EMAIL_ID, DOB, GENDER);
        this.allottedBus = allottedBus;
        this.scanedTickectList = scanedTickectList;
    }

    public Conductor(String name, String EMAIL_ID, String DOB, Gender GENDER, String allottedBus) {
        super(name, EMAIL_ID, DOB, GENDER);
        this.allottedBus = allottedBus;
        scanedTickectList = new ArrayList<>();
    }

    public Conductor(Conductor conductorUser) {
        super(conductorUser.name, conductorUser.EMAIL_ID, conductorUser.DOB, conductorUser.GENDER);
        this.scanedTickectList = conductorUser.scanedTickectList;
        this.allottedBus =conductorUser.allottedBus;
    }

    public String getAllottedBus() {
        return allottedBus;
    }

    public void setAllottedBus(String allottedBus) {
        this.allottedBus = allottedBus;
    }


}
