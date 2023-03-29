package application.model.Tickets;

import application.model.Passenger;
import application.model.Path;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TwoWayTicket extends Ticket{

    public TwoWayTicket(String ID, List<Passenger> passengerList, LocalDateTime timeStamp, List<Path> pathList, double cost) {

        super(ID, passengerList, timeStamp, pathList, cost);
        List<Path> pathListCopy = new ArrayList<>(pathList);
        for(Path path: pathList){
            pathListCopy.add(new Path(path.routeCodes,path.destination,path.source,path.distance,path.busType));
        }

        Collections.reverse(pathListCopy);
        pathList.addAll(pathListCopy);
    }
}
