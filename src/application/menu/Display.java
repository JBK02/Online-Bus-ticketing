package application.menu;

import application.utilities.Colors;
import application.utilities.InputValidator;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

public class Display {


    public static int getMenuOption(String title, Enum @NotNull [] menuList) {

        int option;

        while (true) {

            System.out.println(Colors.formatGreen("\n---------(" + title + " menu)----------\n"));
            int count = 1;
            for (Enum subMenu : menuList) {
                System.out.println(" " + count + ". " + subMenu);
                count++;
            }

            option = InputValidator.getNumber();

            if (option > menuList.length || option <= 0) {
                System.out.println(Colors.formatRed(" Invalid input"));
                continue;
            }


            return option - 1;
        }
    }

    public static int getOption(String placeHolder, @NotNull Set<String> list, String[] arr) {

        if (list.size() < 1) {
            System.out.println(Colors.formatRed( "No " + placeHolder + " Available"));
            return -1;
        }

        System.out.println("Available " + placeHolder + ":");
        int count = 1;
        for (String option : list) {
            System.out.println(" " + count + ". " + option);
            count++;
        }
        for (String option : arr) {
            System.out.println(" " + count + ". " + option);
            count++;
        }
        return InputValidator.getNumber("option", 1, list.size() + arr.length) - 1;
    }

    public static int getOption(String placeHolder, Set<String> list) {
        return getOption(placeHolder, list, new String[]{});
    }

    public static int getOption(String placeHolder, Enum @NotNull [] list){
        Set<String> options = new LinkedHashSet<>();
        for(Enum option : list){
            options.add(option.toString());
        }
        return getOption(placeHolder,options);
    }

    public static void printTitle(String title) {
        System.out.println(Colors.formatGreen("\n---------------(" + title + ")-----------------\n"));
    }

    public static void printInstruction(String instruction) {
        System.out.println(Colors.formatYellow("\n" + instruction));
    }

    public static void printReturnState(boolean flag) {
        if (flag)
            System.out.println(Colors.formatPurple("\n------Successfully updated------"));
        else
            System.out.println(Colors.formatRed("\n------Update Failed-----"));
    }

    public static void printEmpty(String placeHolder) {
        System.out.println(Colors.formatRed("No " + placeHolder + " available"));
    }

}
