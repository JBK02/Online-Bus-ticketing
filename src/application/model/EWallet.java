package application.model;

import java.util.ArrayList;
import java.util.List;

public class EWallet {
    public final String EMAIL_ID;
    private double balance;
    private final int pin;

    private final List<Transaction<String>> transactionHistory;

    public EWallet(String EMAIL_ID, int pin) {
        this.EMAIL_ID = EMAIL_ID;
        this.pin = pin;
        this.balance = 0;
        transactionHistory = new ArrayList<>();
    }

    public double getBalance() {
        return balance;
    }

    public boolean addAmount(double balance) {
        if(balance >= 0 && (this.balance + balance < 10000)) {
            this.balance += balance;
            return true;
        }else return false;
    }

    public boolean debitAmount(double balance) {
        if(!(this.balance - balance < 0 )) {
            this.balance -= balance;
            return true;
        }else return false;
    }

    public List<Transaction<String>> getTransactionHistory() {
        return transactionHistory;
    }

    public boolean verifyPin(int pin){
        return (this.pin == pin);
    }
}
