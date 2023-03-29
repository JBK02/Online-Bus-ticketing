package application.dataBase;

import java.util.Hashtable;
import java.util.Map;

class CredentialDB {

    static final Map<String,Byte[]> adminCredentials = new Hashtable<>();
    static final Map<String,Byte[]> clientCredentials = new Hashtable<>();
    static final Map<String,Byte[]> conductorCredentials = new Hashtable<>();

    private CredentialDB() {

    }
}
