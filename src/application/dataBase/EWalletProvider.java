package application.dataBase;


import application.bankAPI.Bank;
import application.menu.Display;
import application.model.EWallet;
import application.utilities.Colors;
import application.Enum.TransactionMessages;
import application.utilities.InputValidator;

import java.util.HashMap;
import java.util.Map;

public class EWalletProvider {

    final Map<String, EWallet> accountList = new HashMap<>();

    private static EWalletProvider instance = null;
    private final long adminAccountNumber;

    public final double MAX_BALANCE = 10000.0;

    private EWalletProvider(){
        adminAccountNumber = new DataBaseManager().getAdminAccountNumber();
    }

    public static EWalletProvider getInstance(){
        if(instance == null)
            instance = new EWalletProvider();

        return instance;
    }


    boolean createAccount(String emailID, int pin){
        if(accountList.containsKey(emailID))
            return false;
        accountList.put(emailID,new EWallet(emailID,pin));
        return true;
    }


    boolean addCredit(double amount, String emailID, long payee){
        if(accountList.containsKey(emailID) && verifyUser(emailID,amount)){
            if(accountList.get(emailID).addAmount(amount)) {
                System.out.println(Bank.getInstance().moneyTransfer(payee, adminAccountNumber, amount));
                return true;
            }
            else
                return false;
        }else
            return false;
    }

    void addCredit(double amount, String emailID, long payee, int pin){
        if(accountList.containsKey(emailID)){
            if(accountList.get(emailID).addAmount(amount)) {
                Bank.getInstance().moneyTransfer(payee, adminAccountNumber, amount, pin);
            }
        }
    }

    TransactionMessages debitCredit(double amount, String emailID){

        if(accountList.containsKey(emailID)){
            if(verifyUser(emailID,amount)) {
                if(accountList.get(emailID).debitAmount(amount))
                    return TransactionMessages.TRANSFER_SUCCESSFUL;
                else
                    return TransactionMessages.INSUFFICIENT_AMOUNT;
            }
            else
                return TransactionMessages.INVALID_PIN;
        }else
            return TransactionMessages.PAYEE_NOT_FOUND;
    }

    double getBalance(String emailID){
        if(accountList.containsKey(emailID)){
            return accountList.get(emailID).getBalance();
        } else
            return -1;
    }

    private boolean verifyUser(String emailID, double amount){
        Display.printTitle("E-Wallet user verification");

        System.out.printf("Confirmation for paying amount : %.2f\n",amount);
        int count = 3;

        while (count >= 0) {
            Display.printInstruction("Pin should only be a 4 digit number");
            int pin = InputValidator.getNumber("pin", 1000, 9999);
            if (pin < 0)
                return false;

            if(accountList.get(emailID).verifyPin(pin))
                return true;
            else{
                System.out.println(Colors.formatRed("Invalid pin"));
                count--;
            }
        }

        return false;
    }
}
