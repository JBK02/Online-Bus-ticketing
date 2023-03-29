package application.utilities;

public class Colors {


    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_PURPLE = "\u001B[35m";

    public static String formatRed(String input) {
        return ANSI_RED + input + ANSI_RESET;
    }

    public static String formatYellow(String input){
        return ANSI_YELLOW + input + ANSI_RESET;
    }

    public static String formatPurple(String input){
        return ANSI_PURPLE + input + ANSI_RESET;
    }

    public static String formatGreen(String input){
        return ANSI_GREEN + input + ANSI_RESET;
    }



}
