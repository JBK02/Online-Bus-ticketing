package application.dataBase;

import application.model.Bus;
import application.model.Conductor;
import application.Enum.BusType;
import application.Enum.Gender;
import application.Enum.UserType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ValueInitializer {

    private ValueInitializer(){

    }

    public static void initialize() {

        DataBase.conductorUserList.put("john@gmail.com",new Conductor("John","john@gmail.com","01/01/20001", Gender.MALE,"TN 32 DR 6423"));
        DataBase.conductorUserList.put("amal@gmail.com",new Conductor("Amal","amal@gmail.com","02/02/2002", Gender.MALE, "TN 09 AB 1234"));

        Map<String,LinkedHashMap<String, Integer>> routes = DataBase.routes;
        //route 550
        routes.put("550", new LinkedHashMap<>());
        routes.get("550").put("Solinganalur", 0);
        routes.get("550").put("Padur", 3);
        routes.get("550").put("Kalambakum", 4);
        routes.get("550").put("Mellakotaiyur", 7);
        routes.get("550").put("Tagor College", 4);
        routes.get("550").put("Vandalur", 9);
        routes.get("550").put("Tambaram", 10);

        //Buses on route 550
        DataBase.BusRoutes.put("TN 32 DR 6423", new Bus("TN 32 DR 6423", "550", new DataBaseManager().getConductor("john@gmail.com"), "05:00", "07:30", 40, BusType.NORMAL));
        DataBase.availableRouteBusTypes.put("550", new ArrayList<>(List.of(BusType.NORMAL)));
        CredentialManager.addClientCredential("john@gmail.com", "John123@", UserType.CONDUCTOR);

        //route 555G
        routes.put("555G", new LinkedHashMap<>());
        routes.get("555G").put("Solinganalur", 0);
        routes.get("555G").put("Padur", 3);
        routes.get("555G").put("Kalambakum", 4);
        routes.get("555G").put("Mellakotaiyur", 7);
        routes.get("555G").put("Tagor College", 4);
        routes.get("555G").put("Vandalur", 9);
        routes.get("555G").put("Urapakam", 5);
        routes.get("555G").put("Guduvanchery", 2);

        //Buses on route 555G
        DataBase.BusRoutes.put("TN 09 AB 1234", new Bus("TN 09 AB 1234", "555G", new DataBaseManager().getConductor("amal@gmail.com"), "05:00", "07:30", 40, BusType.NORMAL));
        DataBase.availableRouteBusTypes.put("555G", new ArrayList<>(List.of(BusType.NORMAL)));
        CredentialManager.addClientCredential("amal@gmail.com", "Amal123@", UserType.CONDUCTOR);


        DataBase.priceMap.put(BusType.NORMAL, 0.8);
        DataBase.priceMap.put(BusType.DELUXE, 1.0);
        DataBase.priceMap.put(BusType.AC, 1.2);

        DataBase.minimumBusFair.put(BusType.NORMAL, 5.0);
        DataBase.minimumBusFair.put(BusType.DELUXE, 7.0);
        DataBase.minimumBusFair.put(BusType.AC, 11.0);

        DataBase.cancellationFeePercentage = 5;

        CredentialManager.addClientCredential("Admin", "Admin123@", UserType.ADMIN);
        UserFactory.createUserAccount(UserType.CLIENT, "Bala", "bala@gmail.com", "02/02/2001", Gender.MALE, "Bala123@");

    }
}
