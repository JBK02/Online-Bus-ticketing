package application.Enum.ClientMenu;

public enum AccountOption {

    VIEW_PROFILE("View Profile"),
    ADD_BANK_ACCOUNT("Add Bank Details"),
    CREATE_E_WALLET_ACCOUNT("Create E-Wallet Account"),
    ADD_E_WALLET_CREDIT("Add credit to E-Wallet"),
    BACK("Back"),
    QUIT("Quit");


    private final String description;

    AccountOption(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }
}
