package application.menu;


import application.Enum.UserType;
import application.utilities.Colors;
import application.Enum.MainMenuOptions;

public class MainMenu implements Menu{

    public MainMenu() {

    }

    @Override
    public void start() {

        System.out.println(Colors.formatYellow("Enter 'Q' at any input period to quit the operation."));

        while (true) {
            switch (MainMenuOptions.values()[Display.getMenuOption("Main", MainMenuOptions.values())]) {
                case ADMIN -> LogIn.adminLogin();
                case CLIENT -> LogIn.showUserHomePage(UserType.CLIENT);
                case CONDUCTOR -> LogIn.showUserHomePage(UserType.CONDUCTOR);
                case QUIT -> {
                    return;
                }
            }
        }
    }
}
