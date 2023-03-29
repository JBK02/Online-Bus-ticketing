package application;

import application.menu.MainMenu;
import application.menu.Menu;
import application.dataBase.ValueInitializer;

public class Application {

    public void start(){
        ValueInitializer.initialize();
        Menu mainMenu = new MainMenu();
        mainMenu.start();
    }
}
