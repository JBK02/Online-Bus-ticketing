package application.utilities;

import application.menu.Display;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.DayOfWeek;
import java.util.*;

public class InputValidator {

    private final static String ALPHA_NUMERIC = "^[a-zA-Z0-9]*$";
    private final static String NUMBERS = "^[0-9]*$";
    private final static String PRICE = "\\d{1,4}.\\d{1,2}";
    private final static String NAME = "^[a-zA-Z_]+( [a-zA-Z_]+)*$";
    private final static String VEHICLE_REG_NUM = "^[A-Z]{2}[ -][0-9]{1,2}(?: [A-Z])?(?: [A-Z]*)? [0-9]{4}$";
    private final static String PASSWORD = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
    private final static String EMAIL = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(.(com|net|org))*$";
    private final static String TIME = "^([0-1][0-9]|2[0-3]):[0-5][0-9]$";
    private final static String COMMA_SEPARATED_NUMBER = "^([1-7](,[1-7]){1,6})|[1-7]$";
    private final static String DOB = "^([0-2][0-9]||3[0-1])/(0[0-9]||1[0-2])/([1-9][0-9][0-9][0-9])$";

    private static final Scanner SCANNER = new Scanner(System.in);

    private static @NotNull String getInput(String placeHolder, String regex){
        while(true) {
            System.out.print("\n Enter the " + placeHolder + ": ");

            String input = SCANNER.nextLine();

            if (input.equalsIgnoreCase("Q")) {
                return input;
            }

            if (input.isBlank() || !input.matches(regex)) {
                System.out.println(Colors.formatRed(" Invalid Input"));
                continue;
            }


            return input;
        }
    }

    public static int getNumber(String placeHolder,int minValue, int maxValue){

        while (true) {
            String input = getInput(placeHolder,NUMBERS);

            //To check integer max_value
            if(input.length() > 9){
                System.out.println(Colors.formatRed("Invalid input"));
                continue;
            }

            if(input.equalsIgnoreCase("Q"))
                return -1;

            int number = Integer.parseInt(input);

            if(number > maxValue || number < minValue){
                System.out.println(Colors.formatRed("Input should be between " + minValue + " and " + maxValue));
                continue;
            }

            return number;
        }
    }

    public static double getPrice(String placeHolder, double maxValue, double minValue){
        Display.printInstruction("Input should have only 2 decimal places\n");
        while (true) {
            String input = getInput(placeHolder, PRICE);

            if(input.equalsIgnoreCase("Q"))
                return -1.0;

            //To check double max_value
            if(input.length() > 9){
                System.out.println(Colors.formatRed("Invalid input"));
                continue;
            }

            double number = Double.parseDouble(input);

            if(number > maxValue || number < minValue){
                System.out.println(Colors.formatRed("Input should be between " + minValue + " and " + maxValue));
                continue;
            }

            return number;
        }
    }

    public static int getNumber(String placeHolder){
        return getNumber(placeHolder,1,999999999);
    }

    public static int getNumber(){
        return getNumber("input");
    }

    public static @NotNull String getAlphaNumeric(String placeHolder){

            return getInput(placeHolder,ALPHA_NUMERIC);
    }

    public static @NotNull String getName(String placeHolder, int maxLength){
        while (true) {
            String input = getInput(placeHolder,NAME);

            if(input.length() > maxLength){
                System.out.println(Colors.formatRed(" Input is too long"));
                continue;
            }

            return input;
        }
    }

    public static @NotNull String  getName(){
        return getName("input",20);
    }


    public static @NotNull String getVehicleRegNum() {

        return getInput("bus registration number", VEHICLE_REG_NUM);

    }


    public static @NotNull String getPassword(){
        Display.printInstruction("""
                            Password should have at least one uppercase and lowercase alphabet,
                                one special character and a number. Minimum length should be 8.
                            """);
        while (true){
            System.out.print(" Enter password: ");

            String input = SCANNER.nextLine();

            if(input.equals("")){
                System.out.println(Colors.formatRed("Invalid Input"));
                continue;
            }

            if(input.equalsIgnoreCase("Q")){
                return input;
            }

            if(input.length() < 8){
                System.out.println(Colors.formatRed("Input should be longer than or equal to 8 characters"));
            }

            if(!input.matches(PASSWORD)){
                System.out.println(Colors.formatRed("Enter a valid password"));
                continue;
            }

            return input;
        }
    }

    public static @NotNull String getEmail() {

        return getInput("E-Mail ID",EMAIL).toLowerCase(Locale.ROOT);

    }

    public static @NotNull String getTime(String placeHolder){
        Display.printInstruction("Enter time in the format HH:MM, example 05:59");
        return getInput(placeHolder + "time",TIME);
    }

    public static @NotNull String getDOB() {
        Display.printInstruction("DOB should be in DD/MM/YYYY format");

        while (true) {
            String input = getInput("DOB",DOB);

            if(input.equalsIgnoreCase("Q"))
                return input;

            if (!DateFormatter.isDateValid(input)) {
                System.out.println(Colors.formatRed(" Enter a valid date"));
                continue;
            }

            return input;
        }

    }

    public static boolean getYN(){
        do {
            System.out.print("Enter Y or N : ");
            String input = SCANNER.nextLine();
            if (input.equalsIgnoreCase("Y"))
                return true;
            else if (input.equalsIgnoreCase("N"))
                return false;
            else System.out.println(Colors.formatRed("Invalid Input"));
        }while (true);
    }

    public static @Nullable List<DayOfWeek> getDayOfWeek(){
        System.out.println("Days of week:");
        int count = 1;
        for(DayOfWeek dayOfWeek: DayOfWeek.values()){
            System.out.printf(" %d. %s\n",count++, dayOfWeek.name());
        }

        List<DayOfWeek> dayOfWeeks = new ArrayList<>();
        do{
            String input = getInput("option",COMMA_SEPARATED_NUMBER);
            if(input.equalsIgnoreCase("Q"))
                return null;
            String[] numList = input.split(",");
            for(String number: numList){
                try {
                    int index = Integer.parseInt(number) - 1;
                    if(dayOfWeeks.contains(DayOfWeek.values()[index])){
                        System.out.println(Colors.formatRed("Do not repeat the input"));
                        dayOfWeeks = new ArrayList<>();
                        break;
                    }
                    dayOfWeeks.add(DayOfWeek.values()[index]);
                }catch (Exception e){
                    System.out.println(Colors.formatRed("Invalid Input"));
                }
            }
            if(!dayOfWeeks.isEmpty())
                return dayOfWeeks;
        }while (true);
    }

}
