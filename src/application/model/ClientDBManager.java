package application.model;

import application.Enum.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ClientDBManager {

    Client getProfile();
    double getTicketCost(List<Path> pathList, int passengerCount, TicketType ticketType);
    List<String> getStops(String routeCode);
    Set<String> getAllStops();
    Set<String> getAllRoutesWith(String source);

    int getDistance(String source,String destination, String routeCode);
    List<String> findRoutesWith(String source, String destination);

    Map<String,String> findAvailableBuses(String busStop);
    List<String> getBusTypesInRoutes(List<String> routeCodes);

    double getTicketCost(int passengerCount, BusType busType, TicketType ticketType);

    boolean createEWalletAccount( int pin);

    boolean addEWalletCredit(double amount);


    double getEWalletBalance();

    long createBankAccount(int pin);

    void changeCurrentLocation(String location);

    double getPlanCost(List<Path> pathList, int repeatCount);


    void addPassenger(Passenger passenger);

    void addFavRoute(String name,List<Path> pathList);

    double getCancellationFee(String ticketID);


}
