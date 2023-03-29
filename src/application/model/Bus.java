package application.model;

import application.Enum.BusType;
import org.jetbrains.annotations.Nullable;

public class Bus {
    public final String REGISTRATION_NUMBER;
    public @Nullable String routeCode;
    public @Nullable Conductor conductorUser;
    public final String startTime;
    public final String endTime;
    public final int halfTripTime;

    public final BusType busType;


    public Bus(String REGISTRATION_NUMBER, @Nullable String routeCode, @Nullable Conductor conductorUser, String startTime, String endTime, int halfTripTime, BusType busType) {
        this.REGISTRATION_NUMBER = REGISTRATION_NUMBER;
        this.routeCode = routeCode;
        this.conductorUser = conductorUser;
        this.startTime = startTime;
        this.endTime = endTime;
        this.halfTripTime = halfTripTime;

        this.busType = busType;
    }

    @Override
    public String toString() {
        //System.out.println("\n Registration number  RouteCode   Conductor ID         Start Time    End Time     Frequency\n");
        return String.format("   %-13s\t\t%-5s\t\t%-20s %s\t\t  %s\t\t\t %d",REGISTRATION_NUMBER,(routeCode != null)? routeCode:"null", (conductorUser != null)? conductorUser.name : "null", startTime, endTime,halfTripTime);
    }


}
