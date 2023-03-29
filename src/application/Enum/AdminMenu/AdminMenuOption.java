package application.Enum.AdminMenu;

public enum AdminMenuOption {
    UPDATE_DB("Update DB"),
    DISPLAY_DB("Display DB"),
    LOGOUT("LogOut"),
    QUIT("Quit");

    private final String description;

    AdminMenuOption(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }
}
