package application.model;

import application.utilities.DateFormatter;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;

public class Plan {
    public final String ID;
    public final double cost;
    public final  List<DayOfWeek> weekDays;
    public final int repeatCount;
    private final Map<String,Boolean> dateValidity;
    public final Map<String,List<Path>> pathMap;
    public final LocalDateTime purchaseTimeStamp;

    /**
     * For Custom Plan
     */
    public Plan(String ID,List<Path> pathList, double cost, List<DayOfWeek> weekDays, int repeatCount) {
        this.ID = ID;
        this.cost = cost;
        this.dateValidity = new HashMap<>();
        this.pathMap = new HashMap<>();
        this.purchaseTimeStamp = LocalDateTime.now();
        this.weekDays = weekDays;
        this.repeatCount = repeatCount;


        LocalDateTime now = LocalDateTime.now();

        for(int count = 0; count < repeatCount; count++){
            now = now.plusDays(1);

            if(weekDays.contains(DayOfWeek.from(now))) {
                List<Path> pathListCopy = new ArrayList<>();
                for (Path path : pathList)
                    pathListCopy.add(new Path(path));

                dateValidity.put(DateFormatter.getStringDate(now), true);
                pathMap.put(DateFormatter.getStringDate(now), pathListCopy);

            }
        }
    }

    /**
     * For Student Plan
     */
    public Plan(String ID,List<Path> pathList, double cost, List<DayOfWeek> weekDays){
        this.ID = ID;
        this.cost = cost;
        this.dateValidity = new HashMap<>();
        this.pathMap = new HashMap<>();
        this.purchaseTimeStamp = LocalDateTime.now();
        this.weekDays = weekDays;
        this.repeatCount = 28;


        LocalDateTime now = LocalDateTime.now();

        for(int count = 0; count < repeatCount; count++){
            now = now.plusDays(1);

            if(weekDays.contains(DayOfWeek.from(now))) {
                List<Path> pathListCopy = new ArrayList<>();
                for (Path path : pathList)
                    pathListCopy.add(new Path(path));

                dateValidity.put(DateFormatter.getStringDate(now), true);
                pathMap.put(DateFormatter.getStringDate(now), pathListCopy);

            }
        }
    }

    /**
     * For Monthly_Plan
     */
    public Plan(String ID,List<Path> pathList, double cost){
        this.ID = ID;
        this.cost = cost;
        this.dateValidity = new HashMap<>();
        this.pathMap = new HashMap<>();
        this.purchaseTimeStamp = LocalDateTime.now();
        this.weekDays = Arrays.asList(DayOfWeek.values());
        this.repeatCount = 28;


        LocalDateTime now = LocalDateTime.now();

        for(int count = 0; count < repeatCount; count++) {
            now = now.plusDays(1);
            List<Path> pathListCopy = new ArrayList<>();
            for (Path path : pathList)
                pathListCopy.add(new Path(path));

            dateValidity.put(DateFormatter.getStringDate(now), true);
            pathMap.put(DateFormatter.getStringDate(now), pathListCopy);
        }
    }

    public boolean isValidNow(){
        return dateValidity.getOrDefault(DateFormatter.getCurrentDate(), false);
    }

    public void updateValidityOfToday(){
        dateValidity.replace(DateFormatter.getCurrentDate(),false);
    }

    public int getRepeatCount() {
        return this.repeatCount;
    }
}
