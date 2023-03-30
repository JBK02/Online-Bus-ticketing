package application.menu;

import application.dataBase.ClientService;
import application.dataBase.EWalletProvider;
import application.model.ClientDBManager;
import application.model.Client;
import application.model.Passenger;
import application.model.Path;
import application.model.Plan;
import application.model.Tickets.Ticket;
import application.utilities.Colors;
import application.utilities.DateFormatter;
import application.Enum.*;
import application.Enum.clientMenu.AccountOption;
import application.Enum.clientMenu.ClientMenuOption;
import application.utilities.InputValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.DayOfWeek;
import java.util.*;

public class ClientMenu implements Menu {

    ClientDBManager clientDBManager;
    ClientService clientServiceManager;

    public ClientMenu(ClientDBManager clientDBManager, ClientService clientServiceManager) {
        this.clientDBManager = clientDBManager;
        this.clientServiceManager = clientServiceManager;
    }

    @Override
    public void start() {

        changeCurrentLocation();
        System.out.println();
        MainMenu();

        do{
            switch (ClientMenuOption.values()[Display.getMenuOption("Client", ClientMenuOption.values())]){
                case CHANGE_CURRENT_LOCATION -> changeCurrentLocation();
                case BOOK_TICKET -> bookTicket();
                case ADD_PLAN -> addPlan();
                case ADD_FAVOURITE_ROUTE -> addFavRoute();
                case DISPLAY_FAVOURITE_ROUTE -> displayFavRoute();
                case ADD_PASSENGER -> addPassenger();
                case CANCEL_TICKET -> cancelTicket();
                case ACCOUNT -> new AccountMenuOption(clientDBManager).start();
                case BOARD_BUS -> boardBus();
                case LOGOUT -> {
                    return;
                }
                case QUIT-> System.exit(0);
            }
        }while (true);
    }


    private void MainMenu(){


    }


    private void bookTicket() {
        Display.printTitle("Ticket Booking");
        Client profile = clientDBManager.getProfile();

        if(profile.getBankAccountNumber() == -1){
            System.out.println(Colors.formatRed("\nNo bank details have been given to make payment"));
            return;
        }

        //Getting ticket type
        int index = Display.getOption("ticket type", TicketType.values());
        if (index < 0)
            return;
        TicketType ticketType = TicketType.values()[index];

        System.out.println();//Empty line

        //Getting passenger list to be added to the ticket
        int count = InputValidator.getNumber("number of passenger(Excluding you)", 0, 5);
        if(count < 0) return;
        List<Passenger> passengerList = new ArrayList<>();
        passengerList.add(new Passenger(profile.name, profile.DOB, profile.GENDER));

        Map<String, Passenger> existingPassengers = profile.passengerList;

        System.out.println();

        String ID;

        for (int i = 1; i <= count; i++) {

            Display.printInstruction("\nEnter passenger " + i + " details:");

            if (existingPassengers.isEmpty()) {
                Passenger passenger = getPassenger();
                if (passenger == null)
                    return;
                boolean flag = true;
                for(Passenger passenger1 : passengerList){
                    if(passenger1.Name.equalsIgnoreCase(passenger.Name)) {
                        System.out.println(Colors.formatRed("Passenger already added"));
                        i--;
                        flag = false;
                    }
                }
                if(flag)
                    passengerList.add(passenger);
                continue;
            }

            index = Display.getOption("passengers", existingPassengers.keySet(), new String[]{"New passenger"});
            if (index < 0)
                return;
            else if (index == existingPassengers.size()) {
                Passenger passenger = getPassenger();
                if (passenger == null)
                    return;
                boolean flag = true;
                for(Passenger passenger1 : passengerList){
                    if(passenger1.Name.equalsIgnoreCase(passenger.Name)) {
                        System.out.println(Colors.formatRed("Passenger already added"));
                        i--;
                        flag = false;
                    }
                }
                if(flag)
                    passengerList.add(passenger);
            } else if (index < existingPassengers.size()) {
                String emailID = new ArrayList<>(existingPassengers.keySet()).get(index);
                passengerList.add(existingPassengers.get(emailID));
                existingPassengers.remove(emailID);
            }
        }


        if (ticketType == TicketType.ONE_WAY || ticketType == TicketType.TWO_WAY) {
            //Getting path for the ticket
            Display.printInstruction("\nChoose the route in which you want to travel");//New line
            List<Path> pathList;
            if (profile.favouriteRoutes.isEmpty()) {
                pathList = getPathList();
            } else {
                System.out.println("Do you wish to select: \n 1. Favourite path\n 2. New path");
                int option = InputValidator.getNumber("option", 1, 2);
                if(option < 0)
                    return;
                pathList = switch (option) {
                    case 1 -> getFavRoute();
                    case 2 -> getPathList();
                    default -> null;
                };
            }

            if (pathList == null || pathList.isEmpty())
                return;


            System.out.println("Ticket details:\n");

            System.out.println("Ticket type : " + ticketType.getDescription());
            printPath(pathList);
            printPassengers(passengerList);
            System.out.printf("\nPrice : %.2f\n", clientDBManager.getTicketCost(pathList, passengerList.size(),ticketType));

            System.out.println("\n Confirmation for booking the ticket");
            boolean flag = InputValidator.getYN();
            if (flag) {
                ID = clientServiceManager.bookTicket(pathList, passengerList, ticketType, null);
            } else {
                System.out.println(Colors.formatRed("Booking is canceled"));
                return;
            }


        } else if (ticketType == TicketType.ONE_DAY) {

            index = Display.getOption("bus type",BusType.values());
            if(index < 0)
                return;

            BusType busPreference = BusType.values()[index];

            System.out.println("Ticket details:\n");

            System.out.println("Ticket type : " + ticketType.getDescription());
            System.out.println("Path : No restriction");
            printPassengers(passengerList);
            System.out.printf("\nPrice : %.2f\n", clientDBManager.getTicketCost(passengerList.size(),busPreference,ticketType));

            System.out.println("\n Confirmation for booking the ticket");
            boolean flag = InputValidator.getYN();
            if (flag) {
                ID = clientServiceManager.bookTicket( null, passengerList, ticketType, busPreference);
            } else {
                System.out.println(Colors.formatRed("Booking is canceled"));
                return;
            }

        } else return;

        Display.printTitle("Payment Options");
        TransactionMessages returnState;
        if(profile.getEWalletID() == null){
            System.out.println("\nPayment using bank transfer:\n");
            returnState = clientServiceManager.pay(ID,PaymentMethod.BANK_TRANSFER);
        }else {
            System.out.println("\nHow do you want to make payment:\n");
            int option = Display.getOption("payment methods", PaymentMethod.values());
            if (option < 0)
                return;

            PaymentMethod paymentMethod = PaymentMethod.values()[option];
            returnState = clientServiceManager.pay(ID,paymentMethod);
        }

        System.out.println(Colors.formatPurple(returnState.getDescription()));

    }

    private void displayFavRoute(){

        Map<String,List<Path>> favouriteRoutes = clientDBManager.getProfile().favouriteRoutes;
        if(favouriteRoutes.isEmpty()){
            Display.printEmpty("favourite route");
        }

        int count = 1;
        for(String name : favouriteRoutes.keySet()){
            System.out.println("\n"+ count + ". Name: " + name);
            printPath(favouriteRoutes.get(name));
        }

        System.out.println();
    }

    private @Nullable List<Path> getFavRoute(){
        System.out.println("Choose the path to use");
        displayFavRoute();
        Set<String> favPath = clientDBManager.getProfile().favouriteRoutes.keySet();
        int index = InputValidator.getNumber("option",1,favPath.size()) -1;
        if(index < 0)
            return null;

        String emailID = new ArrayList<>(favPath).get(index);

        return clientDBManager.getProfile().favouriteRoutes.get(emailID);
    }

    private void addFavRoute(){
        Display.printTitle("Adding Favourite Route");

        String name;
        do {
            name = InputValidator.getName("name of favourite route", 20);
            if (name.equalsIgnoreCase("Q"))
                return;
            if(clientDBManager.getProfile().favouriteRoutes.containsKey(name)) {
                System.out.println(Colors.formatRed(name + "already exist"));
                continue;
            }

            break;

        }while (true);

        List<Path> pathList = getPathList();
        if(pathList == null || pathList.isEmpty())
            return;

        System.out.println("   Name: " + name);
        printPath(pathList);

        System.out.println("\n Are the details given correct?");
        boolean flag = InputValidator.getYN();
        if(flag)
            clientDBManager.addFavRoute(name,pathList);

        Display.printReturnState(flag);
    }

    private void addPassenger(){
        Display.printTitle("Add passenger");

        Passenger passenger = getPassenger();
        if(passenger == null)
            return;

        for(Passenger passenger1 : clientDBManager.getProfile().passengerList.values()){
            if(passenger1.Name.equals(passenger.Name)){
                System.out.println("Passenger already added");
                return;
            }
        }

        System.out.println(passenger);

        System.out.println("\nConfirm whether you want to add these details");
        boolean flag = InputValidator.getYN();
        if(flag)
            clientDBManager.addPassenger(passenger);

        Display.printReturnState(flag);

    }

    private void cancelTicket(){
        Display.printTitle("Ticket Cancellation");

        Map<String, Ticket> availableTickets = clientDBManager.getProfile().validTickets;
        if(availableTickets.isEmpty()){
            System.out.println(Colors.formatRed("\nNo tickets available"));
            return;
        }

        printTickets(availableTickets);

        int index = InputValidator.getNumber("option",1,availableTickets.size()) - 1;
        if(index < 0)
            return;

        String ticketID = new ArrayList<>(availableTickets.keySet()).get(index);

        System.out.println(Colors.formatYellow(String.format("\n\tTicket Price     : Rs %.2f",availableTickets.get(ticketID).cost)));
        System.out.println(Colors.formatYellow(  String.format("\tCancellation fee : Rs %.2f",clientDBManager.getCancellationFee(ticketID))));

        boolean confirmation = InputValidator.getYN();

        if(confirmation)
            Display.printReturnState(clientServiceManager.cancelTicket(ticketID));
        else
            System.out.println(Colors.formatRed("Ticket cancellation has been abandoned"));
    }

    private void changeCurrentLocation() {
        Display.printTitle("Change Current Location");

        Set<String> busStops = new TreeSet<>(clientDBManager.getAllStops());

        int index;
        do{
            index = Display.getOption("bus stops",busStops);

            if(index < 0){
                System.out.println(Colors.formatRed("Enter valid stop location"));
                continue;
            }
            break;
        }while (true);

        String currentStop = new ArrayList<>(busStops).get(index);

        clientDBManager.changeCurrentLocation(currentStop);

    }

    private void boardBus(){
        Display.printTitle("Boarding Bus");

        Client profile = clientDBManager.getProfile();

        if(profile.validTickets.isEmpty() && profile.validPlans.isEmpty()) {
            System.out.println(Colors.formatRed("\nNo tickets or plan available"));
            return;
        }

        System.out.println("Current location : " + profile.getCurrentLocation());

        Map<String,Ticket> availableTickets = new HashMap<>();
        Map<String,Plan> availablePlan = new HashMap<>();

        Set<String> availableRoutes = clientDBManager.getAllRoutesWith(profile.getCurrentLocation());



        for (Ticket ticket: profile.validTickets.values()){
            if(ticket.pathList == null){
                availableTickets.put(ticket.ID,ticket);
                continue;
            }

            for(Path path : ticket.pathList){
                if(!path.isVisited()){
                    for (String routeCode: availableRoutes){
                        if(path.routeCodes.contains(routeCode)){
                            if(path.source.equals(profile.getCurrentLocation())) {
                                availableTickets.put(ticket.ID, ticket);
                                break;
                            }
                        }
                    }
                }
            }
        }

        for(Plan plan: profile.validPlans.values()){
            if(plan.pathMap.containsKey(DateFormatter.getCurrentDate()) && plan.isValidNow())
                for(Path path: plan.pathMap.get(DateFormatter.getCurrentDate())) {
                    if (!path.isVisited()) {
                        for (String routeCode : availableRoutes) {
                            if (path.routeCodes.contains(routeCode) && path.source.equals(profile.currentLocation)) {
                                availablePlan.put(plan.ID, plan);
                                break;
                            }
                        }
                    }
                }
        }

        if(availableTickets.isEmpty() && availablePlan.isEmpty()){
            System.out.println(Colors.formatRed("No ticket or plan available for current location"));
            return;
        }

        Map<String,String> availableBuses = clientDBManager.findAvailableBuses(profile.currentLocation);

        if(availableBuses.isEmpty()){
            System.out.println(Colors.formatRed("No bus available at current location"));
            return;
        }

        //Get bus from user
        Display.printInstruction("Choose a bus to board");
        int count = 1;
        System.out.println("S.No  Bus Registration Number    Route Code");
        for(Map.Entry<String,String> entry: availableBuses.entrySet()){
            System.out.printf(" %d.    %s               %s\n",count++,entry.getKey(),entry.getValue());
        }

        int index = InputValidator.getNumber("option",1,availableBuses.size()) - 1;
        if(index < 0)
            return;
        String busRegistrationNumber = new ArrayList<>(availableBuses.keySet()).get(index);


        //Get Ticket from user
        String ID;
        if(availablePlan.isEmpty() && !availableTickets.isEmpty()) {
            Display.printInstruction("Choose a ticket to use");
            printTickets(availableTickets);

            index = InputValidator.getNumber("option", 1, availableTickets.size()) - 1;
            if(index < 0)
                return;
            ID = new ArrayList<>(availableTickets.values()).get(index).ID;
        }else if(availableTickets.isEmpty() && !availablePlan.isEmpty()){//Get Plan from user
            Display.printInstruction("Choose a plan to use");
            printPlans(availablePlan,1);

            index = InputValidator.getNumber("option", 1,availablePlan.size()) - 1;
            if(index < 0)
                return;
            ID = new ArrayList<>(availablePlan.values()).get(index).ID;
        }else {
            //Get ticket or plan from user
            Display.printInstruction("Choose a Ticket or Plan to use");
            printTickets(availableTickets);
            System.out.println();
            printPlans(availablePlan,availableTickets.size() + 1);

            index = InputValidator.getNumber("option",1,availablePlan.size() + availableTickets.size()) - 1;
            if(index < 0)
                return;
            if(index < availableTickets.size()){
                ID = new ArrayList<>(availableTickets.values()).get(index).ID;
            }else {
                ID = new ArrayList<>(availablePlan.values()).get(index).ID;
            }
        }

        boolean returnState = clientServiceManager.boardBus(busRegistrationNumber,ID);


        if(returnState){
            System.out.println(Colors.formatPurple("Successfully arrived at destination"));
            if(clientDBManager.getProfile().validTickets.containsKey(ID) && clientDBManager.getProfile().validTickets.get(ID).pathList == null){
                List<String> stops = clientDBManager.getStops(availableBuses.get(busRegistrationNumber));
                stops.remove(profile.currentLocation);
                Display.printInstruction("Choose the in which you want to get down");

                for(count = 1; count < stops.size(); count++){
                    System.out.println(" " + count + ". " + stops.get(count));
                }
                index = InputValidator.getNumber("option",1, stops.size()) -1;
                if(index < 0) return;
                clientDBManager.getProfile().setCurrentLocation(stops.get(index));

            }
            System.out.println("Current Location: " + clientDBManager.getProfile().currentLocation);

        }else
            System.out.println(Colors.formatRed("Boarding Failed"));


    }

    private void addPlan(){
        Display.printTitle("Adding New Plan");

        if(clientDBManager.getProfile().getBankAccountNumber() == -1){
            System.out.println(Colors.formatRed("\nNo bank details have been given to make payment"));
            return;
        }

        int index = Display.getOption("plan",PlanType.values());
        if(index < 0)
            return;
        PlanType planType = PlanType.values()[index];

        System.out.println();

        //Getting path for the plan
        Display.printInstruction("\nChoose the route in which you want to travel");//New line
        List<Path> pathList;
        if (clientDBManager.getProfile().favouriteRoutes.isEmpty()) {
            pathList = getPathList();
        } else {
            System.out.println("Do you wish to select: \n 1. Favourite path\n 2. New path");
            int option = InputValidator.getNumber("option", 1, 2);
            if(option < 0) return;
            pathList = switch (option) {
                case 1 -> getFavRoute();
                case 2 -> getPathList();
                default -> null;
            };
        }

        if (pathList == null || pathList.isEmpty())
            return;

        int repeatCount = 28;
        List<DayOfWeek> dayOfWeekList;

        if(planType.equals(PlanType.CUSTOM_PLAN)) {
            Display.printInstruction("Choose the number of days on plan");
            repeatCount = InputValidator.getNumber("number of days", 2, 62);
            if (repeatCount < 0)
                return;

            Display.printInstruction("""
                    Choose the days of the week
                       Ex: if you want to choose Monday and Saturday
                           enter options as "1,6"
                    """);
            dayOfWeekList = InputValidator.getDayOfWeek();
            if(dayOfWeekList == null)
                return;

        }else if(planType.equals(PlanType.MONTHLY_PLAN)) {
            dayOfWeekList = List.of(DayOfWeek.values());
        } else{
            dayOfWeekList = List.of(DayOfWeek.MONDAY,DayOfWeek.TUESDAY,DayOfWeek.WEDNESDAY,DayOfWeek.THURSDAY,DayOfWeek.FRIDAY,DayOfWeek.SATURDAY);
        }

        System.out.println("Plan Details:\n");

        System.out.println("Plan Type: " + planType.getDescription());
        printPath(pathList);
        System.out.print("\nWeek Days: ");
        for (DayOfWeek dayOfWeek: dayOfWeekList){
            System.out.print(dayOfWeek.name() + " ");
        }

        System.out.printf("\nPlan Cost: %.2f\n", clientDBManager.getPlanCost(pathList,repeatCount));

        System.out.println("\n Confirmation for booking the ticket");
        boolean flag = InputValidator.getYN();
        String planID;
        if (flag) {
            planID = clientServiceManager.addPlan(pathList, dayOfWeekList, repeatCount, planType);
        } else {
            System.out.println(Colors.formatRed("Booking is canceled"));
            return;
        }

        payPrice(planID);

    }

    private @Nullable List<Path> getPathList(){

        List<String> pathList = new ArrayList<>();

        Set<String> busStops = new TreeSet<>(clientDBManager.getAllStops());
        if(busStops.isEmpty()){
            Display.printEmpty("bus stops");
            return null;
        }
        ArrayList<String> stopsCopy = new ArrayList<>(busStops);
        int index = Display.getOption("bus stops",busStops);
        if(index < 0)
            return null;

        pathList.add(stopsCopy.get(index));

        Set<String> visitedRouts = new HashSet<>(clientDBManager.getAllRoutesWith(pathList.get(0)));
        List<Path> paths = new ArrayList<>();

        boolean flag;
        int count = 0;

        do{
            Set<String> availableStops = new TreeSet<>();
            List<String> availableRoutes = new ArrayList<>(clientDBManager.getAllRoutesWith(pathList.get(count)));
            if(count != 0){
                availableRoutes.removeAll(visitedRouts);
                if(availableRoutes.isEmpty()) {
                    System.out.println(Colors.formatYellow("No more bus routes available"));
                    return paths;
                }
            }
            for(String route: availableRoutes){
                availableStops.addAll(clientDBManager.getStops(route));
            }

            availableStops.remove(pathList.get(count));
            List<String> availableStopsArray = new ArrayList<>(availableStops);

            index = Display.getOption("next available bus stops",availableStops);
            if(index < 0)
                return null;

            pathList.add(availableStopsArray.get(index));


            System.out.println();//New line
            List<String> availableBusTypes = clientDBManager.getBusTypesInRoutes(availableRoutes);
            index = Display.getOption("bus type",new HashSet<>(availableBusTypes));
            if(index < 0)
                return null;

            BusType busPreference = BusType.values()[index];

            String source = pathList.get(count);
            String destination = pathList.get(count + 1);
            List<String> routeCode = clientDBManager.findRoutesWith(source,destination);

            paths.add(new Path(routeCode,source,destination, clientDBManager.getDistance(source,destination,routeCode.get(0)),busPreference));

            System.out.println("Do you want to continue adding?");
            flag = InputValidator.getYN();

            count++;
            visitedRouts.addAll(availableRoutes);
        }while (flag);

        return paths;
    }

    private @Nullable Passenger getPassenger(){

        String name = InputValidator.getName("name",20);
        if(name.equalsIgnoreCase("Q"))
            return null;


        String age = InputValidator.getDOB();
        if(age.equalsIgnoreCase("Q"))
            return null;

        int index = Display.getOption("gender", Gender.values());
        if(index < 0)
            return null;

        Gender gender = Gender.values()[index];

        return new Passenger(name,age,gender);
    }

    private void printPath(@NotNull List<Path> pathList){
        System.out.print("   Path: " + pathList.get(0).source + " --> " + pathList.get(0).destination);
        for(int index = 1; index < pathList.size(); index++){
            System.out.printf(" --> %s",pathList.get(index).destination);
        }

        System.out.print("\n   Bus preference: " + pathList.get(0).busType);
        for(int index = 1; index < pathList.size(); index++){
            System.out.printf(" --> %s",pathList.get(index).busType);
        }
        System.out.println();
    }

    private void printPassengers(@NotNull List<Passenger> passengerList){
        System.out.println("Passenger List: ");
        for (Passenger passenger: passengerList){
            System.out.println(passenger);
        }
    }


    private void printTickets(@NotNull Map<String, Ticket> ticketMap){
        int count = 1;
        System.out.println("\nS.No     Ticket ID             Source               Destination          Cost       Ticket Type");
        for(Map.Entry<String,Ticket> entry : ticketMap.entrySet()){
            System.out.printf("\n %d.  %-20s    %-20s %-20s %-7.2f    %s",count++,
                    entry.getKey(),
                    entry.getValue().pathList != null?entry.getValue().pathList.get(0).source: "Any",
                    entry.getValue().pathList != null?entry.getValue().pathList.get(entry.getValue().pathList.size() - 1).destination: "Any",
                    entry.getValue().cost,
                    entry.getValue().getClass().getSimpleName());
        }
    }

    private void printPlans(@NotNull Map<String, Plan> planMap, int count){
        System.out.println("\nS.NO     Plan ID               Source               Destination          Cost        Plan Type");
        for (Map.Entry<String, Plan> entry: planMap.entrySet()) {
            List<Path> pathList = entry.getValue().pathMap.entrySet().iterator().next().getValue();
            System.out.printf("\n %d.  %-20s    %-20s %-20s %-7.2f    %s", count++,
                    entry.getKey(),
                    pathList.get(0).source,
                    pathList.get(pathList.size() - 1).destination,
                    entry.getValue().cost,
                    entry.getValue().getClass().getSimpleName());
        }
    }

    private void payPrice(String id){
        Display.printTitle("Payment Options");
        TransactionMessages returnState;
        if(clientDBManager.getProfile().getEWalletID() == null){
            System.out.println("\nPayment using bank transfer:\n");
            returnState = clientServiceManager.pay(id,PaymentMethod.BANK_TRANSFER);
        }else {
            System.out.println("\nHow do you want to make payment:\n");
            int option = Display.getOption("payment methods", PaymentMethod.values());
            if (option < 0)
                return;

            PaymentMethod paymentMethod = PaymentMethod.values()[option];
            returnState = clientServiceManager.pay(id,paymentMethod);
        }

        System.out.println(Colors.formatPurple(returnState.getDescription()));
    }

}


class AccountMenuOption implements Menu{

    ClientDBManager clientDBManager;

    AccountMenuOption(ClientDBManager clientManager){
        this.clientDBManager = clientManager;
    }

    @Override
    public void start() {
        do{
            switch (AccountOption.values()[Display.getMenuOption("Account", AccountOption.values())]){
                case VIEW_PROFILE -> viewProfile();
                case ADD_BANK_ACCOUNT -> addBankAccount();
                case CREATE_E_WALLET_ACCOUNT -> createEWalletAccount();
                case ADD_E_WALLET_CREDIT -> addEWalletCredit();
                case BACK -> {
                    return;
                }
                case QUIT-> System.exit(0);
            }
        }while (true);
    }

    void addBankAccount(){
        Display.printTitle("Adding Bank Details");

        Client profile = clientDBManager.getProfile();

        if(profile.getBankAccountNumber() != -1) {
            System.out.println(Colors.formatRed("Account already added"));
            return;
        }

        String emailID = profile.EMAIL_ID;
        System.out.println("EmailID : " + emailID);

        Display.printInstruction("Pin should only be a 4 digit number");
        int pin = InputValidator.getNumber("pin", 1000,9999);
        if(pin < 0)
            return;

        long accountNumber = clientDBManager.createBankAccount(pin);
        if(accountNumber == 0)
            System.out.println(Colors.formatRed("Failed"));
        System.out.println(Colors.formatPurple("Details updated successfully. Use this pin for money transfer using bank"));
    }

    void createEWalletAccount(){
        Display.printTitle("Creating E-Wallet Account");

        Client profile = clientDBManager.getProfile();

        if(profile.getEWalletID() != null){
            System.out.println(Colors.formatRed("E-Wallet account already created"));
            return;
        }

        if(profile.getBankAccountNumber() == -1){
            System.out.println(Colors.formatRed("Add bank details before creating E-Wallet account"));
            return;
        }

        String emailID = profile.EMAIL_ID;
        System.out.println("EmailID : " + emailID);

        Display.printInstruction("Pin should only be a 4 digit number");
        int pin = InputValidator.getNumber("pin", 1000,9999);
        if(pin < 0)
            return;


        Display.printInstruction("Confirm that details are correct");
        boolean flag = InputValidator.getYN();

        if(flag){
            Display.printReturnState(clientDBManager.createEWalletAccount(pin));
        }else
            System.out.println(Colors.formatRed("\nAccount creation has been canceled"));


    }

    void addEWalletCredit(){
        Display.printTitle("Adding E-Wallet Credit");

        double currentBalance = clientDBManager.getEWalletBalance();
        if(currentBalance == -1) {
            System.out.println(Colors.formatRed("No account available"));
            return;
        }
        System.out.printf("\nCurrent balance : " + currentBalance + "\n");

        Display.printInstruction(String.format("Enter value between %.2f and %.2f", 100.0, EWalletProvider.getInstance().MAX_BALANCE - currentBalance));
        double amount = InputValidator.getPrice("amount in Rs", EWalletProvider.getInstance().MAX_BALANCE - currentBalance,100);
        if(amount < 0)
            return;


        Display.printReturnState(clientDBManager.addEWalletCredit(amount));
    }

    private void viewProfile(){
        Client profile = clientDBManager.getProfile();
        Display.printTitle("Profile");
        System.out.println();
        System.out.println("Username     : " + profile.name);
        System.out.println("Email-ID     : " + profile.EMAIL_ID);
        System.out.println("DOB          : " + profile.DOB);
        System.out.println("Gender       : " + profile.GENDER);
    }

}

