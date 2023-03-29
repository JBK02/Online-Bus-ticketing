package application.dataBase;

import application.bankAPI.Bank;
import application.Enum.*;
import application.model.*;
import application.model.Bus;
import application.model.Plan;
import application.model.Tickets.OneDayTicket;
import application.model.Tickets.OneWayTicket;
import application.model.Tickets.Ticket;
import application.model.Tickets.TwoWayTicket;
import application.utilities.DateFormatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

class DataBaseManager implements AdminDBManager, ClientDBManager, ConductorDBManager {

    private final long adminAccountNumber;

    private final Client client;
    private final Conductor conductor;


    DataBaseManager() {
        adminAccountNumber = Bank.getInstance().createAccount("Admin@gmail.com",1234);

        client = null;
        conductor = null;
    }

    //To  create ClientManger or ConductorDBManager
    DataBaseManager(String emailID, @NotNull UserType userType){
        adminAccountNumber = Bank.getInstance().createAccount("Admin@gmail.com",1234);

        if(userType.equals(UserType.CLIENT)){
            client = DataBase.clientList.get(emailID);
            conductor = null;
        }else
        {
            conductor = DataBase.conductorUserList.get(emailID);
            client = null;
        }
    }


    //Admin Functions
    @Override
    public double getPrice(BusType busType) {
        return DataBase.priceMap.get(busType);
    }

    @Override
    public void setPrice(double price, BusType busType) {
        DataBase.priceMap.replace(busType,price);
    }


    @Override
    public boolean addRoute(String route, LinkedHashMap<String,Integer> map){

        try {
            DataBase.routes.put(route, new LinkedHashMap<>());
            DataBase.availableRouteBusTypes.put(route,new ArrayList<>());

            for (Map.Entry<String, Integer> element : map.entrySet()) {
                DataBase.routes.get(route).put(element.getKey(), element.getValue());
            }
            return true;

        }catch (NullPointerException e){
            return false;
        }
    }

    @Override
    public boolean removeRoute(String routeCode) {
        try {
            //Todo refunding
            DataBase.routes.remove(routeCode);
            DataBase.availableRouteBusTypes.remove(routeCode);
            Set<String> busList = getAllBusList();

            for (String bus : busList) {
                String busCode = findBusRouteCode(bus);
                if (busCode != null && busCode.equals(routeCode)) {
                    Bus busCopy = getBus(bus);
                    busCopy.routeCode = null;
                    DataBase.BusRoutes.remove(routeCode);
                    DataBase.unAllocatedBus.put(busCode, busCopy);
                }
            }
            return true;

        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public boolean addStop(String route, String stop, int distance, int index) {

        try {
            //Case 1: if the new stop need to be added at the end
            if (index == DataBase.routes.get(route).size()) {
                DataBase.routes.get(route).put(stop, distance);
                return true;
            }

            ArrayList<Map.Entry<String, Integer>> routeCopy = new ArrayList<>(DataBase.routes.get(route).entrySet());

            if (index == 0) {//case 2: index == 0, if the new stop need to add at first
                routeCopy.set(0, Map.entry(routeCopy.get(0).getKey(), distance));
                routeCopy.add(0, Map.entry(stop, 0));

            } else if (routeCopy.get(index + 1).getValue() == distance) { //If new and old shop have same distance
                throw new RuntimeException();

            } else {//Case 3: if the new stop is added in between stops
                routeCopy.add(index, Map.entry(stop, distance));
                routeCopy.set(index + 1, Map.entry(routeCopy.get(index + 1).getKey(), routeCopy.get(index + 1).getValue() - distance));
            }

            DataBase.routes.get(route).clear();

            for (Map.Entry<String, Integer> entry : routeCopy) {
                DataBase.routes.get(route).put(entry.getKey(), entry.getValue());
            }
            return true;

        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean removeStop(String route, String stop){
        try {
            DataBase.routes.get(route).remove(stop);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean addBus(String busRegistrationNumber, String route, String startTime, String endTime,int halfTripTime, BusType type) {
        try {

            Conductor available = getAvailableConductor();
            if (available != null) {
                assignConductor(available.EMAIL_ID, busRegistrationNumber);
            }

            if (available != null) {
                DataBase.BusRoutes.put(busRegistrationNumber, new Bus(busRegistrationNumber, route, available, startTime, endTime, halfTripTime, BusType.NORMAL));
                DataBase.availableRouteBusTypes.get(route).add(type);
            } else {
                DataBase.unAllocatedBus.put(busRegistrationNumber, new Bus(busRegistrationNumber, route, null, startTime, endTime, halfTripTime, BusType.NORMAL));
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean removeBus(String busRegistrationNumber){
        try {
            //TODO refunding and check
            Bus bus = getBus(busRegistrationNumber);

            if(bus == null)
                return false;

            if(bus.conductorUser != null) {
                freeConductor(bus.conductorUser.EMAIL_ID);
            }

            String routeCode = bus.routeCode;

            if (DataBase.BusRoutes.containsKey(busRegistrationNumber)) {
                DataBase.BusRoutes.remove(busRegistrationNumber);
                DataBase.availableRouteBusTypes.get(routeCode).remove(bus.busType);
            }
            else DataBase.unAllocatedBus.remove(busRegistrationNumber);


            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    /*
     * Only available conductors can be swapped
     */
    public boolean changeConductor(String busRegistrationNumber, String emailID) {
        try {
            Bus current = getBus(busRegistrationNumber);

            if(current.routeCode != null){
                if(current.conductorUser != null) {
                    freeConductor(current.conductorUser.EMAIL_ID);
                }
                current.conductorUser = getConductor(emailID);
                assignConductor(emailID,busRegistrationNumber);

                if(DataBase.unAllocatedBus.containsKey(busRegistrationNumber)){
                    DataBase.unAllocatedBus.remove(busRegistrationNumber);
                    DataBase.BusRoutes.put(busRegistrationNumber, current);
                    DataBase.availableRouteBusTypes.get(current.routeCode).add(BusType.NORMAL);

                }
            }else return false;


            return true;
        }catch (Exception e){

            return false;
        }
    }

    @Override
    public Set<String> getAvailableBusList(){
        Set<String> busList = new HashSet<>(DataBase.BusRoutes.keySet());
        return new HashSet<>(busList);
    }

    @Override
    public Set<String> getAllBusList() {
        Set<String> busList = new HashSet<>();
        busList.addAll(DataBase.BusRoutes.keySet());
        busList.addAll(DataBase.unAllocatedBus.keySet());

        return new HashSet<>(busList);
    }

    @Override
    public Set<String> getUnavailableBusList(){
        Set<String> busList = new HashSet<>(DataBase.unAllocatedBus.keySet());
        return new HashSet<>(busList);
    }

    @Override
    public Set<String> getRoutesCodes(){
        return new HashSet<>(DataBase.routes.keySet());
    }

    @Override
    public Map<String,Integer> getRoute(String rootCode) {

        return new LinkedHashMap<>(DataBase.routes.get(rootCode));
    }

    @Override
    public List<String> getStops(String routeCode) {
        return new ArrayList<>(DataBase.routes.get(routeCode).keySet());
    }

    @Override
    public Set<String> getAvailableConductorList() {
        return getAvailableConductors();
    }

    @Override
    public boolean changeBusRoute(String busRegistrationNumber, String routeCode) {
        try {
            Bus current = getBus(busRegistrationNumber);
            if (current == null)
                return false;
            current.routeCode = routeCode;

            if (DataBase.unAllocatedBus.containsKey(busRegistrationNumber) && routeCode != null) {
                DataBase.BusRoutes.put(busRegistrationNumber, current);
                DataBase.availableRouteBusTypes.get(routeCode).add(BusType.NORMAL);
                DataBase.unAllocatedBus.remove(busRegistrationNumber);
            } else if (routeCode == null) {
                DataBase.BusRoutes.remove(busRegistrationNumber);
                DataBase.unAllocatedBus.put(busRegistrationNumber,  current);
            }

            if (routeCode == null && current.conductorUser != null) {
                freeConductor(current.conductorUser.EMAIL_ID);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public @Nullable String findBusRouteCode(String busRegistrationNumber) {
        try{
            return getBus(busRegistrationNumber).routeCode;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public Bus getBus(String busRegistrationNumber){

        if(DataBase.BusRoutes.containsKey(busRegistrationNumber))
            return DataBase.BusRoutes.get(busRegistrationNumber);

        else return DataBase.unAllocatedBus.getOrDefault(busRegistrationNumber, null);
    }

    List<BusType> getBusTypes(String routeCode){
        try{
            List<BusType> busTypes = new ArrayList<>();
            for(BusType type: DataBase.availableRouteBusTypes.get(routeCode)){
                if(!busTypes.contains(type)){
                    busTypes.add(type);
                }
            }
            return busTypes;
        } catch (Exception e){
            return null;
        }
    }

    long getAdminAccountNumber(){
        return adminAccountNumber;
    }

    int getPin(){
        return 1234;
    }


    //ClientDBManager Functions
    void addClient(String name, String emailID, String DOB, Gender gender){
        DataBase.clientList.put(emailID,new Client(name,emailID, DOB, gender));
    }


    @Override
    public Client getProfile() {
        return new Client(client);
    }

    @Override
    public List<String> findRoutesWith(String source, String destination){
        List<String> routesList = new ArrayList<>();
        List<String> busStops;
        for(String route: getRoutesCodes()){
            busStops = getStops(route);
            if(busStops.contains(source) && busStops.contains(destination))
                routesList.add(route);
        }

        return routesList;
    }

    @Override
    public double getTicketCost(@NotNull List<Path> pathList, int passengerCount, TicketType ticketType) {
        double totalCost = 0;
        for(Path path: pathList){
            double cost = path.distance * getPrice(path.busType);
            totalCost += (cost < DataBase.minimumBusFair.get(path.busType))? DataBase.minimumBusFair.get(path.busType):cost;
        }

        if(ticketType.equals(TicketType.TWO_WAY))
            return totalCost * passengerCount * 2;
        else
            return totalCost * passengerCount;
    }

    @Override
    public double getTicketCost(int passengerCount, BusType busType, @NotNull TicketType ticketType){
        if(ticketType.equals(TicketType.ONE_DAY))
            return passengerCount * switch (busType){
                case NORMAL -> 400;
                case DELUXE -> 700;
                case AC -> 1000;
            };
        else return -1;
    }

    @Override
    public double getPlanCost(@NotNull List<Path> pathList, int repeatCount){
        double totalCost = 0;
        for(Path path: pathList){
            double cost = path.distance * getPrice(path.busType);
            totalCost += (cost < DataBase.minimumBusFair.get(path.busType))? DataBase.minimumBusFair.get(path.busType):cost;
        }

        return totalCost * repeatCount;
    }

    @Override
    public int getDistance(String source, String destination, String routeCode) {
        int distance = 0;

        Map<String,Integer> route = getRoute(routeCode);
        LinkedList<String> busStops = new LinkedList<>(route.keySet());

        int min = Math.min(busStops.indexOf(source),busStops.indexOf(destination));
        int max = Math.max(busStops.indexOf(source),busStops.indexOf(destination));

        for(int index = min+1; index <= max; index++){
            distance += route.get(busStops.get(index));
        }

        return distance;
    }

    @Override
    public Set<String> getAllStops(){
        Set<String> busStops = new HashSet<>();

        for(String buRegistrationNumber: getAvailableBusList()){
            String route = findBusRouteCode(buRegistrationNumber);
            busStops.addAll(getStops(route));
        }

        return busStops;
    }

    @Override
    public Set<String> getAllRoutesWith(String busStop){
        Set<String> routesList = new HashSet<>();
        List<String> busStops;

        for(String buRegistrationNumber: getAvailableBusList()){
            String route = findBusRouteCode(buRegistrationNumber);
            busStops = getStops(route);
            if(busStops.contains(busStop))
                routesList.add(route);
        }

        return routesList;
    }

    @Override
    public Map<String,String> findAvailableBuses(String busStop){
        Set<String> routeList = getAllRoutesWith(busStop);
        Set<String> busList = getAvailableBusList();

        Map<String,String> availableBuses = new HashMap<>();
        for(String busRegistrationNumber: busList){
            if(routeList.contains(findBusRouteCode(busRegistrationNumber))){
                availableBuses.put(busRegistrationNumber,findBusRouteCode(busRegistrationNumber));
            }
        }

        return availableBuses;
    }

    @Override
    public List<String> getBusTypesInRoutes(List<String> routeCodes){

        List<String> busTypes = new ArrayList<>();
        int count = 0;

        for(int index = 0; index < routeCodes.size() && count < 3; index++){
            List<BusType> busTypeList = getBusTypes(routeCodes.get(index));
            if(busTypeList.isEmpty())
                continue;
            for (BusType busType: busTypeList){
                if(busType.equals(BusType.NORMAL) && !busTypes.contains(BusType.NORMAL.name())){
                    busTypes.add(busType.name());
                    count++;
                }else if(busType.equals(BusType.DELUXE) && !busTypes.contains(BusType.DELUXE.name())){
                    busTypes.add(busType.name());
                    count++;
                }else if(busType.equals(BusType.AC) && !busTypes.contains(BusType.AC.name())){
                    busTypes.add(busType.name());
                    count++;
                }
            }

        }

        return busTypes;
    }

    @Override
    public boolean createEWalletAccount(int pin) {
        if(client.getEWalletID() != null)
            return false;

        boolean returnState =  EWalletProvider.getInstance().createAccount(client.EMAIL_ID,pin);
        if(returnState)
            client.setEWalletID(client.EMAIL_ID);

        return returnState;
    }

    @Override
    public boolean addEWalletCredit(double amount) {
        return EWalletProvider.getInstance().addCredit(amount,client.EMAIL_ID,client.getBankAccountNumber());
    }


    @Override
    public double getEWalletBalance() {
        return EWalletProvider.getInstance().getBalance(client.EMAIL_ID);
    }

    @Override
    public long createBankAccount(int pin){
        if(client.getBankAccountNumber() == -1) {
            long accountNUmber = Bank.getInstance().createAccount(client.EMAIL_ID, pin);
            client.setBankAccountNumber(accountNUmber);
            return accountNUmber;
        }
        return 0;
    }

    @Override
    public void changeCurrentLocation(String location){
        client.currentLocation = location;
    }

    @Override
    public void addPassenger(Passenger passenger){
        client.passengerList.put(passenger.Name,passenger);
    }

    @Override
    public void addFavRoute(String name, List<Path> pathList){
        client.favouriteRoutes.put(name,pathList);
    }

    @Override
    public double getCancellationFee(String ticketID){
        try {
            return DataBase.ticketMap.get(ticketID).cost * (DataBase.cancellationFeePercentage/100);
        }catch (Exception E){
            return -1.0;
        }
    }


    //Conductor Functions
    void addConductor(String name, String emailID, String DOB, Gender GENDER) {
        DataBase.conductorUserList.put(emailID, new Conductor(name, emailID, DOB, GENDER, null));
        DataBase.availableConductors.add(emailID);
    }

    void assignConductor(String emailID, String allottedBus) {
        DataBase.conductorUserList.get(emailID).setAllottedBus(allottedBus);
        DataBase.availableConductors.remove(emailID);
    }

    void freeConductor(String emailID) {
        DataBase.conductorUserList.get(emailID).setAllottedBus(null);
        DataBase.availableConductors.add(emailID);
    }

    Conductor getConductor(String emailID) {
        return new Conductor(DataBase.conductorUserList.get(emailID));
    }

    public Conductor getAvailableConductor() {
        if (DataBase.availableConductors.isEmpty()) {
            return null;
        } else return new Conductor(DataBase.conductorUserList.get(DataBase.availableConductors.iterator().next()));
    }

    Set<String> getAvailableConductors() {
        return new HashSet<>(DataBase.availableConductors);
    }

    @Override
    public boolean scanTicket(String busRegistrationNumber,String ID) {
        if (DataBase.ticketMap.containsKey(ID))
            return verifyTicket(ID, busRegistrationNumber);
        else if (DataBase.planMap.containsKey(ID))
            return verifyPlan(ID, busRegistrationNumber);
        else return false;
    }

    @Override
    public Conductor getDetails() {
        return new Conductor(conductor);
    }

    @Override
    public List<Ticket> viewScanHistory() {
        return conductor.scanedTickectList;
    }



    //TicketDBManager Function

    String addTicket(List<Path> pathList, List<Passenger> passengerList, TicketType ticketType, double cost){

        try {
            LocalDateTime purchaseTime = LocalDateTime.now();
            String id = String.format("%d%d%d%d%d%d%d", purchaseTime.getHour(), purchaseTime.getMinute(), purchaseTime.getSecond(), purchaseTime.getDayOfMonth(), purchaseTime.getMonth().getValue(), purchaseTime.getYear(), passengerList.get(0).Name.hashCode()%1000);
            switch (ticketType){
                case ONE_DAY -> DataBase.unPaidTickets.put(id,new Ticket(id,passengerList,purchaseTime,null, cost));
                case ONE_WAY -> DataBase.unPaidTickets.put(id, new Ticket(id,passengerList,purchaseTime,pathList,cost));
                case TWO_WAY -> {
                    List<Path> pathListCopy = new ArrayList<>(pathList);
                    for(Path path: pathList){
                        pathListCopy.add(new Path(path.routeCodes,path.destination,path.source,path.distance,path.busType));
                    }
                    List<Path> reversedList = new ArrayList<>(pathList);
                    Collections.reverse(reversedList);
                    pathListCopy.addAll(reversedList);
                    DataBase.unPaidTickets.put(id,new Ticket(id,passengerList,purchaseTime,pathListCopy,cost));
                }
            }

            return id;
        }catch (Exception e) {
            return null;
        }
    }

    Ticket getTicket(String ticketID){
        if(DataBase.ticketMap.containsKey(ticketID)){
            return DataBase.ticketMap.get(ticketID);

        }else return DataBase.unPaidTickets.getOrDefault(ticketID, null);
    }

    Ticket updateUnPaidTicket(String ticketID, PaymentMethod paymentMethod, long payeeAccountNumber, String emailID){
        if(DataBase.unPaidTickets.containsKey(ticketID)){
            Ticket ticket = DataBase.unPaidTickets.get(ticketID);
            DataBase.unPaidTickets.remove(ticketID);
            DataBase.ticketMap.put(ticketID,ticket);

            if(paymentMethod.equals(PaymentMethod.E_WALLET)){
                DataBase.EWalletTransactionHistory.put(ticketID,new Transaction<>(ticketID,emailID,"AdminDBManager@gmail.com",DataBase.ticketMap.get(ticketID).cost));
            }
            else if(paymentMethod.equals(PaymentMethod.BANK_TRANSFER)){
                DataBase.bankTransactionHistory.put(ticketID,new Transaction<>(ticketID,payeeAccountNumber, getAdminAccountNumber(), DataBase.ticketMap.get(ticketID).cost));
            }
            return ticket;
        }else return null;

    }


    boolean verifyTicket(String ticketID, String busRegistrationNumber){
        String routeCode = findBusRouteCode(busRegistrationNumber);

        if(DataBase.ticketMap.containsKey(ticketID) && DataBase.ticketMap.get(ticketID).isValidity()){
            Ticket ticket = DataBase.ticketMap.get(ticketID);
            if(ticket.pathList == null && ticket.purchaseTimeStamp.toLocalDate().equals(LocalDate.now()))
                return true;

            List<Path> pathList = ticket.pathList;
            for(Path path : pathList) {
                if (!path.isVisited() && path.routeCodes.contains(routeCode)) {
                    path.Visited();
                    path.setBusRegistrationNumber(busRegistrationNumber);
                    return true;
                }
                if (pathList.get(pathList.size() - 1).isVisited())
                    ticket.changeValidity();
            }
            return false;
        }
        return false;
    }//TODO if the path in list is valid for current ClientDBManager stop but previous path is not visited?

    String addPlan(String emailID, List<Path> pathList, PlanType planType, List<DayOfWeek> weakDays, int repeatCount, double cost){

        try {
            LocalDateTime purchaseTime = LocalDateTime.now();
            String id = String.format("%d%d%d%d%d%d%d", purchaseTime.getHour(), purchaseTime.getMinute(), purchaseTime.getSecond(), purchaseTime.getDayOfMonth(), purchaseTime.getMonth().getValue(), purchaseTime.getYear(), emailID.hashCode()%1000);
            switch (planType){
                case CUSTOM_PLAN -> DataBase.unPaidPlan.put(id,new Plan(id,pathList, cost,weakDays, repeatCount));
                case MONTHLY_PLAN -> DataBase.unPaidPlan.put(id, new Plan(id,pathList,cost, Arrays.asList(DayOfWeek.values()), 28));
                case STUDENT_PLAN -> DataBase.unPaidPlan.put(id,new Plan(id,pathList,cost, Arrays.asList(
                        DayOfWeek.MONDAY,
                        DayOfWeek.TUESDAY,
                        DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY,
                        DayOfWeek.FRIDAY,
                        DayOfWeek.SATURDAY),
                        28));
            }

            return id;
        }catch (Exception e) {
            return null;
        }
    }

    Plan getPlan(String planID){
        if(DataBase.planMap.containsKey(planID)){
            return DataBase.planMap.get(planID);

        }else return DataBase.unPaidPlan.getOrDefault(planID, null);
    }

    Plan updateUnPaidPlan(String planID, PaymentMethod paymentMethod, long payeeAccountNumber, String emailID){
        if(DataBase.unPaidPlan.containsKey(planID)){
            Plan plan = DataBase.unPaidPlan.get(planID);
            DataBase.unPaidPlan.remove(planID);
            DataBase.planMap.put(planID,plan);

            if(paymentMethod.equals(PaymentMethod.E_WALLET)){
                DataBase.EWalletTransactionHistory.put(planID,new Transaction<>(planID,emailID,"AdminDBManager@gmail.com",DataBase.planMap.get(planID).cost));
            }
            else if(paymentMethod.equals(PaymentMethod.BANK_TRANSFER)){
                DataBase.bankTransactionHistory.put(planID,new Transaction<>(planID,payeeAccountNumber, adminAccountNumber, DataBase.planMap.get(planID).cost));
            }
            return plan;
        }else return null;

    }

    boolean verifyPlan(String planID, String busRegistrationNumber){
        String routeCode = findBusRouteCode(busRegistrationNumber);

        if(DataBase.planMap.containsKey(planID) && DataBase.planMap.get(planID).isValidNow()){
            Plan plan = DataBase.planMap.get(planID);
            List<Path> pathList = plan.pathMap.get(DateFormatter.getCurrentDate());

            for(Path path : pathList) {
                if (!path.isVisited() && path.routeCodes.contains(routeCode)) {
                    path.Visited();
                    path.setBusRegistrationNumber(busRegistrationNumber);
                    if (pathList.get(pathList.size() - 1).isVisited())
                        plan.updateValidityOfToday();

                    return true;
                }
            }
            return false;
        }
        return false;
    }


}
