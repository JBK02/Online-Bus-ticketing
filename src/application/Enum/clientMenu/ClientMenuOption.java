package application.Enum.clientMenu;

public enum ClientMenuOption {
    CHANGE_CURRENT_LOCATION("Change Current Location"),
    BOOK_TICKET("Book Ticket"),
    ADD_PLAN("Add Plan"),
    ADD_FAVOURITE_ROUTE("Add Favourite Route"),
    DISPLAY_FAVOURITE_ROUTE("Display Favourite Route"),
    ADD_PASSENGER("Add Passenger"),
    CANCEL_TICKET("Cancel Ticket"),
    ACCOUNT("Account"),
    BOARD_BUS("Board Bus"),
    LOGOUT("Logout"),
    QUIT("Quit");

    private final String description;

    ClientMenuOption(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }
}
