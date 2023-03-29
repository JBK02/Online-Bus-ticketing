package application.Enum.AdminMenu;

public enum DisplayDBOption {
    LIST_BUS("List available buses"),
    LIST_ROUTE("List bus routes"),
    BACK("Back"),
    QUIT("Quit");

    private final String description;

    DisplayDBOption(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return this.description;
    }
}
