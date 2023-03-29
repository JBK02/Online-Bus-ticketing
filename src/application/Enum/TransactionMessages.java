package application.Enum;

public enum TransactionMessages {
    PAYEE_NOT_FOUND("Payee account number is incorrect"),
    RECIPIENT_NOT_FOUND("Recipient account number is incorrect"),
    INSUFFICIENT_AMOUNT("Insufficient amount in account"),
    TRANSFER_SUCCESSFUL("Transfer successful"),
    INVALID_PIN("Invalid pin"),
    TRANSFER_FAIL("Transfer fail");

    private final String description;

    TransactionMessages(String description) {
        this.description = description;
    }

    public String getDescription(){
        return this.description;
    }
}
