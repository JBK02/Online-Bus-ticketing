package application.dataBase;

import application.Enum.BusType;
import application.model.Bus;
import application.model.Client;
import application.model.Conductor;
import application.model.Plan;
import application.model.Tickets.Ticket;
import application.model.Transaction;

import java.util.*;

class DataBase {

    //Bus Data
    final static Map<String, LinkedHashMap<String,Integer>> routes = new HashMap<>();
    final static Map<String, Bus> BusRoutes = new HashMap<>();
    final static Map<String, Bus> unAllocatedBus = new HashMap<>();
    final static Map<BusType, Double> priceMap = new HashMap<>();
    final static Map<BusType, Double> minimumBusFair = new HashMap<>();
    final static Map<String, List<BusType>> availableRouteBusTypes = new HashMap<>();
    static double cancellationFeePercentage = 0;

    //Client Data
    final static Map<String, Client> clientList = new HashMap<>();

    //Conductor Data
    final static Map<String, Conductor> conductorUserList = new HashMap<>();
    final static Set<String> availableConductors = new HashSet<>();

    //Ticket Data
    final static Map<String, Ticket> ticketMap = new HashMap<>();
    final static Map<String, Ticket> unPaidTickets = new HashMap<>();
    final static Map<String, Ticket> canceledTicket = new HashMap<>();

    //Plan Data
    final static Map<String, Plan> planMap = new HashMap<>();
    final static Map<String, Plan> unPaidPlan = new HashMap<>();

    //Banking and EWallet History
    final static Map<String, Transaction<Long>> bankTransactionHistory = new HashMap<>();
    final static Map<String, Transaction<String>> EWalletTransactionHistory = new HashMap<>();
}
