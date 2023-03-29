package application.model;

import application.Enum.BusType;

import java.util.List;

public class Path {
    private boolean isVisited;
    public final List<String> routeCodes;
    public final String source;
    public final String destination;
    public final int distance;
    public final BusType busType;


    private String busRegistrationNumber = null;

    public Path(List<String> routeCodes, String source, String destination, int distance, BusType busType) {
        this.isVisited = false;
        this.routeCodes = routeCodes;
        this.source = source;
        this.destination = destination;
        this.distance = distance;
        this.busType = busType;
    }

    public Path(Path path){
        this.isVisited = path.isVisited();
        this.routeCodes = path.routeCodes;
        this.source = path.source;
        this.destination = path.destination;
        this.distance = path.distance;
        this.busType = path.busType;
    }

    public boolean isVisited() {
        return this.isVisited;
    }

    public void Visited() {
        this.isVisited = true;
    }

    public void setBusRegistrationNumber(String busRegistrationNumber) {
        if(this.busRegistrationNumber == null)
            this.busRegistrationNumber = busRegistrationNumber;
    }
}
