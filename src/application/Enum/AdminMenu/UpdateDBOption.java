package application.Enum.AdminMenu;

public enum UpdateDBOption {
    ADD_ROUTE("Add new route"),
    ADD_STOP("Add new stop"),
    ADD_BUS("Add new bus"),
    REMOVE_ROUTE("Remove Route"),
    REMOVE_STOP("Remove stop"),
    REMOVE_BUS("Remove bus"),
    CHANGE_BUS_ROUTE("Change bus route"),
    CHANGE_CONDUCTOR("Assign bus conductor"),
    CHANGE_PRICE("Set price"),
    BACK("Back"),
    QUIT("Quit");

    private final String description;

    UpdateDBOption(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return this.description;
    }
}
