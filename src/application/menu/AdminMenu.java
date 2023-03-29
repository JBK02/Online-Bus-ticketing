package application.menu;

import application.Enum.adminMenu.AdminMenuOption;
import application.Enum.adminMenu.DisplayDBOption;
import application.model.AdminDBManager;
import application.utilities.Colors;
import application.Enum.adminMenu.UpdateDBOption;
import application.Enum.BusType;
import application.utilities.InputValidator;

import java.util.*;


class AdminMenu implements Menu{

    AdminDBManager adminManager;
    AdminMenuHelper helper;

    public AdminMenu(AdminDBManager adminManager) {
        this.adminManager = adminManager;
        helper = new AdminMenuHelper(adminManager);
    }

    @Override
    public void start() {

        while (true) {
            switch (AdminMenuOption.values()[Display.getMenuOption("Admin", AdminMenuOption.values())]) {
                case UPDATE_DB -> updateDB();
                case DISPLAY_DB -> displayDB();
                case LOGOUT -> {
                    return;
                }
                case QUIT -> System.exit(0);
            }
        }
    }

    private void updateDB(){

        DBUpdater updater = new DBUpdater(adminManager,helper);

        while (true) {
            switch (UpdateDBOption.values()[Display.getMenuOption("DataBase Updating", UpdateDBOption.values())]) {
                case ADD_ROUTE -> updater.addRoute();
                case ADD_STOP -> updater.addStop();
                case ADD_BUS -> updater.addBus();
                case REMOVE_ROUTE -> updater.removeRoute();
                case REMOVE_STOP -> updater.removeStop();
                case REMOVE_BUS -> updater.removeBus();
                case CHANGE_BUS_ROUTE -> updater.changeBusRoute();
                case CHANGE_CONDUCTOR -> updater.changeConductor();
                case CHANGE_PRICE -> updater.changePrice();
                case BACK -> {
                    return;
                }
                case QUIT -> System.exit(0);
            }
        }

    }

    private void displayDB(){

        DisplayDB displayDB = new DisplayDB(adminManager);

        while (true) {
            switch (DisplayDBOption.values()[Display.getMenuOption("View dataBase", DisplayDBOption.values())]) {
                case LIST_BUS -> displayDB.listBuses();
                case LIST_ROUTE -> displayDB.listRoutes();
                case BACK -> {
                    return;
                }
                case QUIT -> System.exit(0);
            }
        }
    }

}


class DBUpdater{

    private final AdminDBManager adminDBManager;
    private final AdminMenuHelper helper;

    public DBUpdater(AdminDBManager adminManager, AdminMenuHelper helper) {
        this.adminDBManager = adminManager;
        this.helper = helper;
    }

    void addRoute(){

        Display.printTitle("Adding New Route");

        LinkedHashMap<String,Integer> routeList = new LinkedHashMap<>();//To store user stop inputs
        String route = helper.getRoute();
        if(route.equalsIgnoreCase("Q")) return;

//      Used for getting loop count
        int stopCount = InputValidator.getNumber("number of stops",2,30);
        if(stopCount < 0) return;

        String instruction = """
                To enter each stops in the route
                    1. Enter the stop name first
                    2. When prompted enter of current stop distance from previous stop
                    note: First stop distance is 0 by default""";
        Display.printInstruction(instruction);

        //Gets every stop, and it's distance from previous stop from user
        String stop;
        int distance;
        for(int i = 1; i <= stopCount; i++) {

            stop = InputValidator.getName("stop " + i + " name",30);
            if(stop.equalsIgnoreCase("Q")){
                return;
            }

            if (i == 1) {
                routeList.put(stop, 0);
                continue;
            }

            if(routeList.containsKey(stop)){
                System.out.println(Colors.formatRed("Stop already exists"));
                i--;
                continue;
            }

            distance = helper.getDistance();
            if(distance == -1) return;

            routeList.put(stop,distance);//Updating the list
        }

        Display.printReturnState(adminDBManager.addRoute(route, routeList));

    }

    void addStop() {
        Display.printTitle("Adding New Stop");

        String route;
        Set<String> routeCodes = adminDBManager.getRoutesCodes();

        if(routeCodes.isEmpty()){
            Display.printEmpty("route");
            return;
        }

        int index = Display.getOption("route", routeCodes);//Structure Input
        if(index < 0)
            return;

        route = (routeCodes.toArray(new String[0]))[index];
        Map<String,Integer> routeStops = adminDBManager.getRoute(route);
        ArrayList<Map.Entry<String,Integer>> routeCopy = new ArrayList<>(routeStops.entrySet());

        //Structure Inputs
        String stop = helper.getStop(route);
        if(stop.equalsIgnoreCase("Q")) return;

        index = Display.getOption("position", routeStops.keySet(),new String[] {" "});
        if(index == -1)
            return;

        int distance;
        if(index > 0 && index < routeStops.size() ) {
            int maxDistance = routeCopy.get(index).getValue() - 1;
            if (maxDistance == 0) {
                System.out.println(Colors.formatRed("Adding stop in here is not possible"));
                return;
            }

            Display.printInstruction("Distance should be less than " + maxDistance + " and non 0 or negative");
            distance = helper.getDistance(maxDistance);
        }
        else {
            distance = helper.getDistance();
        }
        if(distance == -1){
            return;
        }

        Display.printReturnState(adminDBManager.addStop(route,stop,distance,index));
    }

    void addBus(){
        Display.printTitle("Adding New Bus");

        String route;
        Set<String> busList = adminDBManager.getAvailableBusList();
        Set<String> routeCodes = adminDBManager.getRoutesCodes();

        if(routeCodes.isEmpty()){
            Display.printEmpty("route");
            return;
        }

        String instruction = """
                Enter valid bus registration number:
                    Example: TN 32 DR 6423""";
        Display.printInstruction(instruction);
        String busRegistrationNumber;
        while (true) {
            busRegistrationNumber = InputValidator.getVehicleRegNum();
            if(busRegistrationNumber.equalsIgnoreCase("Q")) return;

            if(busList.contains(busRegistrationNumber)){
                System.out.println(Colors.formatRed("Bus already exist"));
                continue;
            }
            break;
        }

        instruction = "Select the bus route assigning to the new bus";
        Display.printInstruction(instruction);
        int index = Display.getOption("route", routeCodes);//Structure Input
        if(index < 0)
            return;
        route = (routeCodes.toArray(new String[0]))[index];

        instruction = "Select the bus type";
        Display.printInstruction(instruction);
        index = Display.getOption("types", BusType.values());
        if (index < 0)
            return;
        BusType type = BusType.values()[index];

        String startTime = InputValidator.getTime("bus starting");
        if(startTime.equalsIgnoreCase("Q"))
            return;

        String endTime = InputValidator.getTime("bus end");
        if (endTime.equalsIgnoreCase("Q"))
            return;

        instruction = "Enter time in minutes";
        Display.printInstruction(instruction);

        int halfTripTime = InputValidator.getNumber("half trip time",10,1000);
        if(halfTripTime < 0)
            return;

        Display.printReturnState(adminDBManager.addBus(busRegistrationNumber,route,startTime,endTime,halfTripTime,type));
    }

    void removeRoute(){
        Display.printTitle("Remove Route");

        Set<String> existingRoutes = adminDBManager.getRoutesCodes();
        if(existingRoutes == null || existingRoutes.isEmpty()){
            System.out.println(Colors.formatRed("No bus routes available"));
            return;
        }

        Display.printInstruction("Select the bus route to be removed");

        int index = Display.getOption("route",existingRoutes);
        if(index < 0) return;

        String routeCode = (existingRoutes.toArray(new String[0]))[index];

        System.out.println(adminDBManager.getRoutesCodes());
        boolean flag = adminDBManager.removeRoute(routeCode);
        System.out.println(adminDBManager.getRoutesCodes());


        Display.printReturnState(flag);
    }

    void removeBus(){

        Display.printTitle("Removing Bus");

        String busRegistrationNumber;
        Set<String> busList = adminDBManager.getAvailableBusList();
        if(busList.isEmpty()){
            System.out.println(Colors.formatRed("No bus routes available"));
            return;
        }

        Display.printInstruction("Select the bus to be removed");

        int index = Display.getOption("registration number", busList);//Structure Input
        if(index < 0) return;

        busRegistrationNumber = (busList.toArray(new String[0]))[index];

        Display.printReturnState(adminDBManager.removeBus(busRegistrationNumber));
    }

    void changePrice() {
        Display.printTitle("Changing Price");


        int index = Display.getOption("bus type", BusType.values());
        if(index < 0) return;
        BusType type = BusType.values()[index];


        System.out.println(Colors.formatYellow("Current price : Rs " + adminDBManager.getPrice(type) + "\n"));

        double price = InputValidator.getPrice("price in Rs ",100,0);
        if(price < 0) return;

        adminDBManager.setPrice(price,type);

        System.out.println("New price : Rs " + adminDBManager.getPrice(type));
        Display.printReturnState(true);
    }

    void removeStop(){
        Display.printTitle("Removing Bus Stop");

        Set<String> existingRoutes = adminDBManager.getRoutesCodes();
        if(existingRoutes.isEmpty()){
            System.out.println(Colors.formatRed("No bus routes available"));
            return;
        }

        String instruction = """
                Select the bus route in which stop should be removed""";
        Display.printInstruction(instruction);

        int index = Display.getOption("route",existingRoutes);
        if(index < 0) return;

        String routeCode = (existingRoutes.toArray(new String[0]))[index];

        Set<String> stopList = adminDBManager.getRoute(routeCode).keySet();
        if(stopList.size() <= 2){
            System.out.println(Colors.formatRed("Only 2 stops available in this route, no more can be removed"));
            return;
        }

        instruction = """
                Select the stop to be removed""";
        Display.printInstruction(instruction);

        index = Display.getOption("registration number", stopList);//Structure Input
        if(index < 0) return;

        String stop = (stopList.toArray(new String[0]))[index];

        Display.printReturnState(adminDBManager.removeStop(routeCode,stop));
    }


    void changeBusRoute(){
        Display.printTitle("Change Bus Route");


        String route;
        String busRegistrationNumber;
        Set<String> busList = adminDBManager.getAllBusList();

        if(busList.isEmpty()){
            Display.printEmpty("bus");
            return;
        }

        ArrayList<String> busListCopy = new ArrayList<>(busList);
        Set<String> routeCodes = adminDBManager.getRoutesCodes();

        if(routeCodes.isEmpty()){
            Display.printEmpty("route");
            return;
        }

        ArrayList<String> routeCodesCopy = new ArrayList<>(routeCodes);

        //create a separate function
        new DisplayDB(adminDBManager).listBuses();
        System.out.println();


        int index = Display.getOption("registration number",busList);
        if(index < 0) return;
        busRegistrationNumber = busListCopy.get(index);

        Display.printInstruction("Select the route which should be assigned to the bus");

        index = Display.getOption("route", routeCodes);
        if(index < 0) return;
        route = routeCodesCopy.get(index);

        Display.printReturnState(adminDBManager.changeBusRoute(busRegistrationNumber,route));

    }

    void changeConductor() {
        Display.printTitle("Change Conductor");

        Set<String> availableConductors = adminDBManager.getAvailableConductorList();

        if (availableConductors.isEmpty()) {
            System.out.println(Colors.formatRed("No conductors available"));
            return;
        }

        Set<String> availableBuses = adminDBManager.getAvailableBusList();
        ArrayList<String> availableCopy = new ArrayList<>(availableBuses);
        Set<String> unavailableBuses = adminDBManager.getUnavailableBusList();
        ArrayList<String> unavailableCopy = new ArrayList<>(unavailableBuses);
        int input;

        if (availableBuses.isEmpty() && unavailableBuses.isEmpty()) {
            Display.printEmpty("bus");
            return;
        }

        System.out.println("\n Registration number  RouteCode   Conductor ID         Start Time    End Time     Frequency\n");
        int count = 1;
        if (!availableBuses.isEmpty()) {
            System.out.println("Available Buses:");
            for (String s : availableBuses) {
                System.out.println(" " + count++ + ". " + adminDBManager.getBus(s).toString());
            }
        }

        if (!unavailableBuses.isEmpty()) {
            System.out.println("Unavailable Buses:");
            for (String s : unavailableBuses) {
                System.out.println(" " + count++ + ". " + adminDBManager.getBus(s).toString());
            }
        }

        input = InputValidator.getNumber("option", 1, availableBuses.size() + unavailableBuses.size()) - 1;
        if(input < 0) return;

        String busRegistrationNumber;

        if (input < availableCopy.size()) {
            busRegistrationNumber = availableCopy.get(input);
        } else {
            busRegistrationNumber = unavailableCopy.get(input - availableCopy.size());
        }

        input = Display.getOption("conductors", availableConductors);
        if(input < 0) return;

        Display.printReturnState(adminDBManager.changeConductor(busRegistrationNumber, new ArrayList<>(availableConductors).get(input)));

    }
}

class DisplayDB{

    AdminDBManager adminDBManager;

    DisplayDB(AdminDBManager adminManager){
        this.adminDBManager = adminManager;
    }

    void listBuses(){
        Display.printTitle("Available Buses");

        new AdminMenuHelper(adminDBManager).displayBus();
    }

    void listRoutes(){
        Display.printTitle("Available Routes");

        Set<String> routeCode = adminDBManager.getRoutesCodes();

        System.out.println("\n Route Code       Bus Stops              Distance \n");
        for (String route  : routeCode){
            System.out.println(route +  " : ");
            Map<String,Integer> stopList = adminDBManager.getRoute(route);

            for (Map.Entry<String,Integer> stop : stopList.entrySet()){
                System.out.printf("%15s%-20s %10d%n"," ",stop.getKey(),stop.getValue());
            }

            System.out.println("\n" + "-".repeat(49));
        }
    }
}

class AdminMenuHelper {

    AdminDBManager adminManager;

    public AdminMenuHelper(AdminDBManager adminManager) {
        this.adminManager = adminManager;
    }

    String getRoute() {

        Set<String> existingRoutes = adminManager.getRoutesCodes();
        String input;

        String instruction = """
                                
                Route Code should
                    1. have only alphabets and numbers
                    2. have maximum of 5 characters and minimum of 2 characters only""";
        Display.printInstruction(instruction);

        while (true) {
            input = InputValidator.getAlphaNumeric("route code");
            if(input.equalsIgnoreCase("Q")) return "Q";

            if(input.charAt(0)  ==  '0'){
                System.out.println(Colors.formatRed("Invalid route code"));
                continue;
            }

            if (input.length() > 5 || input.length() < 2) {
                System.out.println(Colors.formatRed("\nOnly characters size between 2 and 5 are allowed"));
                continue;
            }

            if (existingRoutes.contains(input)) {
                System.out.println(Colors.formatRed("\nroute code already exists"));
                continue;
            }

            return input;
        }
    }


    String getStop(String route){
        while (true) {
            String stop = InputValidator.getName("stop name", 30);
            if(stop.equalsIgnoreCase("Q")) return "Q";

            if(route != null) {
                if (adminManager.getRoute(route).containsKey(stop)) {
                    System.out.println(Colors.formatRed("Stop already exist"));
                    continue;
                }
            }

            return stop;
        }
    }


    int getDistance(int maxValue) {
        int distance;
        while (true) {

            distance = InputValidator.getNumber("distance in km",1,maxValue);
            if(distance < 0) return -1;

            if (distance > maxValue || distance < 1) {
                System.out.println(Colors.formatRed("Invalid input"));
                continue;
            }

            return distance;
        }
    }

    int getDistance(){
        return getDistance(100);
    }

    void displayBus(){
        System.out.println("\n  Registration number  RouteCode   Conductor ID         Start Time    End Time     Frequency\n");
        Set<String> availableBuses = adminManager.getAvailableBusList();
        System.out.println("Allotted Buses:\n");
        if(availableBuses.isEmpty()){
            Display.printEmpty("bus");
        }else {
            for (String s : availableBuses) {
                System.out.println(adminManager.getBus(s).toString());
            }
        }

        Set<String> unAllottedBus = adminManager.getUnavailableBusList();
        System.out.println("\nUn-Allotted Buses:\n");
        if(unAllottedBus.isEmpty()){
            Display.printEmpty("bus");
        }else {
            for(String busNum : unAllottedBus)
                System.out.println(adminManager.getBus(busNum).toString());
        }
    }

}