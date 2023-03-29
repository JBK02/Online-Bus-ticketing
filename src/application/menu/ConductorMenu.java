package application.menu;

import application.model.ConductorDBManager;
import application.model.Ticket;
import application.utilities.Colors;
import application.Enum.ConductorMenu.ConductorMenuOptions;
import application.Enum.UserType;
import application.Enum.UserLoginOptions;

public class ConductorMenu implements Menu{

    ConductorDBManager conductor;

    public ConductorMenu(ConductorDBManager conductor) {
        this.conductor = conductor;
    }

    @Override
    public void start() {

        do{
            switch (ConductorMenuOptions.values()[Display.getMenuOption("Conductor", ConductorMenuOptions.values())]){
                case VIEW_PROFILE -> viewProfile(conductor);
                case VIEW_SCANNED_TICKETS -> printScanList(conductor);
                case LOGOUT -> {
                    return;
                }
                case QUIT-> System.exit(0);
            }
        }while (true);
    }

    private void viewProfile(ConductorDBManager conductor){
        Display.printTitle("Profile");
        System.out.println();
        System.out.println("Username     : " + conductor.getDetails().name);
        System.out.println("Email-ID     : " + conductor.getDetails().EMAIL_ID);
        System.out.println("DOB          : " + conductor.getDetails().DOB);
        System.out.println("Gender       : " + conductor.getDetails().GENDER.toString());
        System.out.println("Allotted bus : " + conductor.getDetails().getAllottedBus());
    }

    private void printScanList(ConductorDBManager conductor){
        Display.printTitle("Scanned Tickets");

        if(conductor.viewScanHistory().isEmpty()){
            System.out.println(Colors.formatRed("No tickets have been scanned"));
            return;
        }

            System.out.println("\nS.No     Ticket ID             Ticket Type     ClientDBManager Email ID");
        int count = 1;
        for(Ticket ticket: conductor.viewScanHistory()){
             System.out.printf(" %d.     %-20s %-10s     %s\n",count++, ticket.ID, ticket.getClass().getSimpleName(), ticket.passengerList.get(0).Name);
        }
    }
}
