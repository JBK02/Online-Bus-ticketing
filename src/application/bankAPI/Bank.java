package application.bankAPI;

import application.menu.Display;
import application.utilities.Colors;
import application.Enum.TransactionMessages;
import application.utilities.InputValidator;

import java.util.HashMap;
import java.util.Map;



/**
 * A singleton bank class which is used to simulate a real bank money transfer
 */
public class Bank{

    private final Map<Long, BankClient> clientList = new HashMap<>();
    private static Bank instance = null;

    private Bank(){

    }

    public static Bank getInstance(){
        if(instance == null) {
            instance = new Bank();
        }
        return instance;
    }

    /**
     * Creates a new account in the bank
     * @return application.bankAPI.Bank account number
     */
    public long createAccount(String emailID, int pin){
        Long accountNumber = (long)(900000000 + ((emailID.hashCode()%10000000) + clientList.size())%1000000000);
        BankClient client = new BankClient(emailID,accountNumber,pin);
        clientList.put(accountNumber,client);
        return accountNumber;
    }

    public TransactionMessages moneyTransfer(Long payee, Long recipient, double amount){

        if(!verifyUser(payee,amount)){
            return TransactionMessages.INVALID_PIN;
        }

        if(clientList.containsKey(payee)) {
            if (clientList.containsKey(recipient)) {
                if (clientList.get(payee).widthDraw(amount)) {
                    if(clientList.get(recipient).deposit(amount))
                        return TransactionMessages.TRANSFER_SUCCESSFUL;
                    else return TransactionMessages.TRANSFER_FAIL;
                }
                else return TransactionMessages.INSUFFICIENT_AMOUNT;

            } else return TransactionMessages.RECIPIENT_NOT_FOUND;
        }
        else return TransactionMessages.PAYEE_NOT_FOUND;
    }

    //Used only by admin for automatic transaction
    public void moneyTransfer(Long payee, Long recipient,double amount, int pin) {

        if (clientList.containsKey(payee) && clientList.get(payee).verifyPin(pin)) {
            if (clientList.containsKey(recipient)) {
                if (clientList.get(payee).widthDraw(amount))
                    clientList.get(recipient).deposit(amount);
            }

        }
    }

    private boolean verifyUser(Long payee, double amount){
        Display.printTitle("Bank account verification");

        System.out.printf("\nConfirmation for paying amount : %.2f\n", amount);
        int count = 3;

        while (count >= 0) {
            Display.printInstruction("Pin should only be a 4 digit number");
            int pin = InputValidator.getNumber("pin", 1000, 9999);
            if (pin < 0)
                return false;

            if(clientList.get(payee).verifyPin(pin))
                return true;
            else {
                System.out.println(Colors.formatRed("Invalid pin"));
                count--;
            }

        }

        return false;
    }

}

/**
 * Class to store, manipulate and transfer money through client account
 */
class BankClient {

    private final String emailID;
    private final Long accountNumber;
    private double savingAmount = 0;
    private final int pin;

    BankClient(String emailID, Long accountNumber, int pin) {
        this.emailID = emailID;
        this.accountNumber = accountNumber;
        this.pin = pin;
    }

    public boolean deposit(double amount) {
        if(amount < 0)
            return false;
        savingAmount = savingAmount + amount;
        return true;
    }

    /**
     * Detection of amount from the account
     * @return boolean to sate success of withdrawal of amount
     */
    public boolean widthDraw(double amount) {
        if (savingAmount - amount < 0) {
            savingAmount = savingAmount - amount;
            return true;
        } else return false;
    }

    boolean verifyPin(int pin){
        return (pin == this.pin);
    }
}


