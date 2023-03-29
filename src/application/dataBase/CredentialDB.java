package application.dataBase;

import java.util.Hashtable;
import java.util.Map;

class CredentialDB {

    private static CredentialDB instance = null;

    private final Map<String,Byte[]> adminCredentials = new Hashtable<>();
    private final Map<String,Byte[]> clientCredentials = new Hashtable<>();
    private final Map<String,Byte[]> conductorCredentials = new Hashtable<>();

    private CredentialDB() {

    }

    static CredentialDB getInstance(){
        if(instance == null){
            return new CredentialDB();
        }

        return instance;
    }

    Map<String, Byte[]> getAdminCredentials() {
        return adminCredentials;
    }

    Map<String, Byte[]> getClientCredentials() {
        return clientCredentials;
    }

    Map<String,Byte[]> getConductorCredentials(){
        return conductorCredentials;
    }
}
