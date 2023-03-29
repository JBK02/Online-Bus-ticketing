package application.dataBase;

import application.model.AdminDBManager;
import application.model.ClientDBManager;
import application.model.ConductorDBManager;
import application.Enum.Gender;
import application.Enum.UserType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.NoSuchAlgorithmException;

public final class UserFactory {

    private UserFactory(){

    }

    public static @Nullable AdminDBManager getAdminDBManager(String userName, String password) throws NoSuchAlgorithmException {
        if(CredentialManager.validateClientCredential(userName,password, UserType.ADMIN)){
            return new DataBaseManager();
        }else {
            return null;
        }
    }

    public static @Nullable ClientDBManager getClientDBManager(String userName, String password) throws NoSuchAlgorithmException{
        if(CredentialManager.validateClientCredential(userName,password, UserType.CLIENT)){
            return new DataBaseManager(userName, UserType.CLIENT);
        }
        else
            return null;
    }

    public static @Nullable ConductorDBManager getConductorDBManager(String userName, String password) throws NoSuchAlgorithmException{
        if(CredentialManager.validateClientCredential(userName,password, UserType.CONDUCTOR)){
            return new DataBaseManager(userName, UserType.CONDUCTOR);
        }
        else
            return null;
    }

    public static @Nullable ClientService getClientService(String emailID, String password) throws NoSuchAlgorithmException{
        if(CredentialManager.validateClientCredential(emailID,password,UserType.CLIENT)){
            return new ClientService(emailID);
        }
        else
            return null;
    }

    public static void createUserAccount(@NotNull UserType user, String userName, String emailID, String DOB, Gender GENDER, String password){

        if(user.equals(UserType.CONDUCTOR)) {
            if (CredentialManager.addClientCredential(emailID, password, user)) {
                new DataBaseManager().addConductor(userName, emailID, DOB, GENDER);
            }
        }
        else if(user.equals(UserType.CLIENT)){
            if (CredentialManager.addClientCredential(emailID, password, user)) {
                new DataBaseManager().addClient(userName, emailID, DOB, GENDER);
            }
        }
    }

    public static boolean isUserExist(String emailID, UserType user){
        return CredentialManager.isUserExist(emailID,user);
    }
}
