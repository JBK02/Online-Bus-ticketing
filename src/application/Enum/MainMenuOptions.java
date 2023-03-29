package application.Enum;


public enum MainMenuOptions {
    ADMIN("Admin"),
    CLIENT("Client"),
    CONDUCTOR("Conductor"),
    QUIT("Quit");

    private final String description;

    MainMenuOptions(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return this.description;
    }
}
