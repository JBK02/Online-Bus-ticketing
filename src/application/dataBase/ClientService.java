package application.dataBase;

import application.Enum.*;
import application.bankAPI.Bank;
import application.model.*;
import application.model.Tickets.Ticket;
import application.utilities.DateFormatter;
import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public class ClientService {

    DataBaseManager dataBaseManager;
    Client client;

    private ClientService(){
    }

    ClientService(String emailID) {
        client = DataBase.clientList.get(emailID);
        dataBaseManager = new DataBaseManager(emailID, UserType.CLIENT);
    }

    public boolean cancelTicket(String ticketID){

        double cancellationAmount;

        if(DataBase.ticketMap.containsKey(ticketID)){

            if(DataBase.bankTransactionHistory.containsKey(ticketID)){

                Transaction<Long> transaction = DataBase.bankTransactionHistory.get(ticketID);
                cancellationAmount = transaction.amount * (DataBase.cancellationFeePercentage/100);
                Bank.getInstance().moneyTransfer(dataBaseManager.getAdminAccountNumber(), transaction.payee, cancellationAmount, dataBaseManager.getPin());
                DataBase.bankTransactionHistory.put("C-" + ticketID,new Transaction<>(ticketID,transaction.recipient, transaction.payee, cancellationAmount));

            }else if(DataBase.EWalletTransactionHistory.containsKey(ticketID)){

                Transaction<String> transaction = DataBase.EWalletTransactionHistory.get(ticketID);
                cancellationAmount = transaction.amount * (DataBase.cancellationFeePercentage/100);
                EWalletProvider.getInstance().addCredit(cancellationAmount,transaction.payee, dataBaseManager.getAdminAccountNumber(), dataBaseManager.getPin());
                DataBase.EWalletTransactionHistory.put("C-" + ticketID, new Transaction<>(ticketID,transaction.recipient,transaction.payee,cancellationAmount));

            }
            DataBase.canceledTicket.put("C-" + ticketID, DataBase.ticketMap.get(ticketID));
            DataBase.ticketMap.remove(ticketID);

            Ticket ticket = client.validTickets.get(ticketID);
            client.validTickets.remove(ticketID);
            client.canceledTickets.put("C-" + ticketID,ticket);
            return true;
        }else return false;
    }

    public boolean boardBus(String busRegistrationNumber, String ID){
        Conductor conductor = dataBaseManager.getBus(busRegistrationNumber).conductorUser;
        if(conductor == null)
            return false;

        boolean returnState =  dataBaseManager.scanTicket(busRegistrationNumber,ID);

        String nextLocation  = client.currentLocation;
        if(returnState) {
            List<Path> pathList;
            if(DataBase.ticketMap.containsKey(ID)) {
                if (DataBase.ticketMap.get(ID).pathList == null)
                    return true;
                pathList = dataBaseManager.getTicket(ID).pathList;
            }else
                pathList = dataBaseManager.getPlan(ID).pathMap.get(DateFormatter.getCurrentDate());

            if (pathList.size() == 1) {
                if(pathList.get(0).busType.compareTo(bus.busType) >= 0)
                    return false;
                nextLocation = pathList.get(0).destination;
            } else {
                int count = 0;
                for (Path path : pathList) {
                    if (!path.isVisited())
                        break;
                    count++;
                }
                if (count >= pathList.size()) {
                    nextLocation = pathList.get(pathList.size() - 1).destination;
                    if(DataBase.ticketMap.containsKey(ID))
                        dataBaseManager.getTicket(ID).changeValidity();
                } else {
                    nextLocation = pathList.get(count - 1).destination;
                }
            }


            //Remove plan or ticket from valid list and then add to history
            if (DataBase.ticketMap.containsKey(ID) && DataBase.ticketMap.get(ID).isValidity()) {
                Ticket ticket = client.validTickets.remove(ID);
                client.ticketHistory.put(ID,ticket);
            } else if(DataBase.planMap.containsKey(ID)){
                if(dataBaseManager.getPlan(ID).purchaseTimeStamp.toLocalDate().plusDays(dataBaseManager.getPlan(ID).getRepeatCount() + 1).isAfter(LocalDate.now())){
                    Plan plan = client.validPlans.remove(ID);
                    client.planHistory.put(ID,plan);
                }
            }
        }

        client.setCurrentLocation(nextLocation);

        return returnState;
    }

    public TransactionMessages pay(String id, PaymentMethod paymentMethod) {

        TransactionMessages returnState;

        //For Ticket
        if(DataBase.unPaidTickets.containsKey(id)) {
            returnState = switch (paymentMethod) {
                case BANK_TRANSFER ->
                        Bank.getInstance().moneyTransfer(client.getBankAccountNumber(), dataBaseManager.getAdminAccountNumber(), dataBaseManager.getTicket(id).cost);
                case E_WALLET -> EWalletProvider.getInstance().debitCredit(dataBaseManager.getTicket(id).cost, client.EMAIL_ID);
            };

            if (returnState == TransactionMessages.TRANSFER_SUCCESSFUL) {
                Ticket ticket = dataBaseManager.updateUnPaidTicket(id, paymentMethod, client.getBankAccountNumber(), client.EMAIL_ID);
                if (ticket != null)
                    client.validTickets.put(id, ticket);
            }else DataBase.unPaidTickets.remove(id);

        }
        else // For Plan
            if(DataBase.unPaidPlan.containsKey(id)){
            returnState = switch (paymentMethod) {
                case BANK_TRANSFER ->
                        Bank.getInstance().moneyTransfer(client.getBankAccountNumber(), dataBaseManager.getAdminAccountNumber(), dataBaseManager.getPlan(id).cost);
                case E_WALLET -> EWalletProvider.getInstance().debitCredit(dataBaseManager.getPlan(id).cost, client.EMAIL_ID);
            };

            if (returnState == TransactionMessages.TRANSFER_SUCCESSFUL) {
                Plan plan = dataBaseManager.updateUnPaidPlan(id, paymentMethod, client.getBankAccountNumber(), client.EMAIL_ID);
                if (plan != null)
                    client.validPlans.put(id, plan);
            }else DataBase.unPaidPlan.remove(id);

        } else returnState = TransactionMessages.TRANSFER_FAIL;


        return returnState;
    }

    public String bookTicket(List<Path> pathList, List<Passenger> passengerList, @NotNull TicketType ticketType, BusType busType){

        String ticketID;
        if(ticketType.equals(TicketType.ONE_DAY))
        {
            ticketID = dataBaseManager.addTicket(null,passengerList,ticketType, dataBaseManager.getTicketCost(passengerList.size(),busType,ticketType));
        }else
            ticketID = dataBaseManager.addTicket(pathList,passengerList,ticketType, dataBaseManager.getTicketCost(pathList,passengerList.size(),ticketType));

        return ticketID;
    }

    public String addPlan(List<Path> pathList, List<DayOfWeek> dayOfWeeks, int repeatCount, PlanType planType){
        return dataBaseManager.addPlan(client.EMAIL_ID,pathList,planType,dayOfWeeks,repeatCount, dataBaseManager.getPlanCost(pathList,repeatCount));
    }
}
