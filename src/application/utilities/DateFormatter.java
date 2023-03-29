package application.utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatter {

    static DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String getCurrentDate() {

        LocalDateTime now = LocalDateTime.now();

        return pattern.format(now);
    }

    public static String getStringDate(LocalDateTime date){

        return pattern.format(date);
    }

    public static boolean isDateValid(String date)
    {
        try {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
