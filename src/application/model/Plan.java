package application.model;

import application.model.Path;
import application.utilities.DateFormatter;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Plan {
    public final String ID;
    public final double cost;
    public final List<DayOfWeek> weekDays;
    public final int repeatCount;
    private final Map<String,Boolean> dateValidity;
    public final Map<String,List<Path>> pathMap;
    public final LocalDateTime purchaseTimeStamp;

    public Plan(String ID,List<Path> pathList, double cost, List<DayOfWeek> weekDays, int repeatCount) {
        this.ID = ID;
        this.cost = cost;
        this.dateValidity = new HashMap<>();
        this.pathMap = new HashMap<>();
        this.purchaseTimeStamp = LocalDateTime.now();
        this.weekDays = weekDays;
        this.repeatCount = repeatCount;


        LocalDateTime now = LocalDateTime.now();

        for(int count = 0; count < repeatCount;){
            now = now.plusDays(1);

            if(weekDays.contains(DayOfWeek.from(now))) {
                List<Path> pathListCopy = new ArrayList<>();
                for (Path path : pathList)
                    pathListCopy.add(new Path(path));

                dateValidity.put(DateFormatter.getStringDate(now), true);
                pathMap.put(DateFormatter.getStringDate(now), pathListCopy);
                count++;
            }
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
