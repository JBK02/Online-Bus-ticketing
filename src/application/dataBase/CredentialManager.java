package application.dataBase;

import application.Enum.UserType;
import application.utilities.Colors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;


final class CredentialManager {

    static CredentialDB credentialDB = CredentialDB.getInstance();


    static boolean validateClientCredential(String userName,String password, UserType user) throws NoSuchAlgorithmException{


        Map<String, Byte[]> userDB = getUserDB(user);

        if (userDB == null)
            return false;

        if (userDB.containsKey(userName)) {
                return Arrays.equals(userDB.get(userName), getPasswordDigest(password));

        } else {
            return false;
        }

    }

    static boolean addClientCredential(String userName,String password, UserType user) {

        Map<String, Byte[]> userDB = getUserDB(user);

        if (userDB.containsKey(userName)) {
            return false;
        }

        try {
            userDB.put(userName, getPasswordDigest(password));
            return true;
        }catch (NoSuchAlgorithmException E){
            System.out.println(Colors.formatRed("SHA-256 algorithm not found in JDK"));
            return false;
        }

    }

    private static Map<String, Byte[]> getUserDB(@NotNull UserType user){

        return  switch(user) {
            case ADMIN -> credentialDB.getAdminCredentials();
            case CLIENT -> credentialDB.getClientCredentials();
            case CONDUCTOR -> credentialDB.getConductorCredentials();
        };
    }


    static boolean isUserExist(String emailID, UserType user){
        return getUserDB(user).containsKey(emailID);
    }

    static Byte @Nullable [] getPasswordDigest(@NotNull String password) throws NoSuchAlgorithmException{

        Byte[] bytes = new Byte[32];
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(password.getBytes());

        int index = 0;
        for (byte value : digest) {
            bytes[index++] = value;
        }
        return bytes;


    }

}
