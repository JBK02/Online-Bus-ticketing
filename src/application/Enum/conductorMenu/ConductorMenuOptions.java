package application.Enum.conductorMenu;

public enum ConductorMenuOptions {
    VIEW_PROFILE("View profile"),
    VIEW_SCANNED_TICKETS("View scanned Tickets"),
    LOGOUT("Logout"),
    QUIT("Quit");

    private final String description;

    ConductorMenuOptions(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }
}
