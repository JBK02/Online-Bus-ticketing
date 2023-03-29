package application.menu;

import application.Enum.UserLoginOptions;
import application.dataBase.ClientService;
import application.model.AdminDBManager;
import application.dataBase.UserFactory;
import application.model.ClientDBManager;
import application.model.ConductorDBManager;
import application.utilities.Colors;
import application.Enum.Gender;
import application.Enum.UserType;
import application.utilities.InputValidator;

import java.security.NoSuchAlgorithmException;

class LogIn {

    static void adminLogin() {
        Display.printTitle("Admin Login Page");

        String userName;
        String password;

        AdminDBManager adminManager;

        while (true) {
            userName = InputValidator.getName("user name", 100);
            if(userName.equalsIgnoreCase("Q"))
                return;
            password = InputValidator.getPassword();
            if(password.equalsIgnoreCase("Q"))
                return;

            try {
                adminManager = UserFactory.getAdminDBManager(userName, password);
                if (adminManager == null) {
                    System.out.println(Colors.formatRed("Invalid Credentials"));
                } else {
                    new AdminMenu(adminManager).start();
                    return;
                }
            }catch (NoSuchAlgorithmException E){
                System.out.println("SHA-256 algorithm not found in JDK, Validation failed");
                return;
            }

        }

    }

    static void showUserHomePage(UserType userType){
        do {
            switch ( UserLoginOptions.values()[Display.getMenuOption("Login/Signup", UserLoginOptions.values())]) {
                case LOGIN -> {
                    if(userType.equals(UserType.CLIENT))
                        clientLogin();
                    else
                        conductorLogin();
                }
                case SIGN_UP -> LogIn.userSignUp(userType);
                case BACK -> {
                    return;
                }
                case QUIT -> System.exit(0);
            }
        } while (true);
    }

    static void clientLogin(){
        Display.printTitle("Client Login Page");

        String emailID;
        String password;

        ClientDBManager clientManager;
        ClientService clientServiceManager;

        while (true){
            emailID = InputValidator.getEmail();
            if(emailID.equalsIgnoreCase("Q"))
                return;

            password = InputValidator.getPassword();
            if(password.equalsIgnoreCase("Q"))
                return;

            try {
                clientManager = UserFactory.getClientDBManager(emailID,password);
                clientServiceManager = UserFactory.getClientService(emailID,password);
                if(clientManager == null || clientServiceManager == null){
                    System.out.println(Colors.formatRed("Invalid Credentials"));
                }
                else {
                    System.out.println(Colors.formatPurple("========Login successfully======\n"));
                    new ClientMenu(clientManager,clientServiceManager).start();
                    return;
                }
            }catch (NoSuchAlgorithmException E){
                System.out.println("SHA-256 algorithm not found in JDK, Validation failed");
                return;
            }

        }
    }

    static void conductorLogin(){
        Display.printTitle("Conductor Login Page");

        String emailID;
        String password;

        ConductorDBManager conductor;

        while (true){
            emailID = InputValidator.getEmail();
            if(emailID.equalsIgnoreCase("Q"))
                return;

            password = InputValidator.getPassword();
            if(password.equalsIgnoreCase("Q"))
                return;

            try {
                conductor = UserFactory.getConductorDBManager(emailID,password);
                if(conductor == null){
                    System.out.println(Colors.formatRed("Invalid Credentials"));
                }
                else {
                    System.out.println(Colors.formatPurple("========Login successfully======\n"));
                    new ConductorMenu(conductor).start();
                    return;
                }
            }catch (NoSuchAlgorithmException E){
                System.out.println("SHA-256 algorithm not found in JDK, Validation failed");
                return;
            }
        }
    }

    static void userSignUp(UserType user){
        Display.printTitle(user.name() + " Signup Page");


        String userName = InputValidator.getName("user name",20);
        if(userName.equalsIgnoreCase("Q"))
            return;

        String emailID;
        do {
            emailID = InputValidator.getEmail();
            if(emailID.equalsIgnoreCase("Q"))
                return;
            if(UserFactory.isUserExist(emailID, user)){
                System.out.println(Colors.formatRed("Email-ID already exist."));
                continue;
            }
            break;
        } while (true);

        if(emailID.equalsIgnoreCase("Q"))
            return;

        String DOB = InputValidator.getDOB();
        if(DOB.equalsIgnoreCase("Q"))
            return;

        int index = Display.getOption("gender",Gender.values());
        if(index < 0) return;
        Gender GENDER = Gender.values()[index];

        String reEnteredPassword;
        String password;
        do {
            password = InputValidator.getPassword();
            if(password.equalsIgnoreCase("Q"))
                return;
            System.out.println("\nRe-Enter password");
            reEnteredPassword = InputValidator.getPassword();
            if(reEnteredPassword.equalsIgnoreCase("Q"))
                return;
        } while (!password.equals(reEnteredPassword));

        UserFactory.createUserAccount(user,userName,emailID,DOB,GENDER,password);
        System.out.println(Colors.formatPurple("Signup successfully completed"));
    }


}
