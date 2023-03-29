package application.model;

public class Transaction<T> {

    public final String transactionID;
    public final T payee;
    public final T recipient;
    public final double amount;

    public Transaction(String transactionID, T payee, T recipient, double amount) {
        this.transactionID = transactionID;
        this.payee = payee;
        this.recipient = recipient;
        this.amount = amount;
    }
}
