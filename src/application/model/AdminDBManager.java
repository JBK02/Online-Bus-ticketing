package application.model;

import application.Enum.BusType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public interface AdminDBManager {


    boolean addRoute(String route, LinkedHashMap<String,Integer> map);
    boolean addStop(String route, String stop,int distance, int index);
    boolean addBus(String busRegistrationNumber, String route, String startTime, String endTime, int halfTripTime, BusType type);
    boolean removeRoute(String route);
    boolean removeStop(String route, String stop);
    boolean removeBus(String busRegistrationNumber);
    void setPrice(double price, BusType busType);
    double getPrice(BusType busType);
    Set<String> getAvailableBusList();
    Set<String> getAllBusList();
    public Set<String> getUnavailableBusList();
    Set<String> getRoutesCodes();
    Map<String,Integer> getRoute(String routeCode);
    Set<String> getAvailableConductorList();


    /**
     * Functions:
     *  (1) If routeCode is not null, just assigns the route code.
     *  (2) Change bus from available list to unAllocated list and frees the conductor if routeCode is null.
     *  (3) Change bus from unAllocated list to allocated list if routeCode is not null.
     */
    boolean changeBusRoute(String busRegistrationNumber, String routeCode);
    boolean changeConductor(String busRegistrationNumber,String emailID);
    String findBusRouteCode(String busRegistrationNumber);
    Bus getBus(String busRegistrationNumber);

}
