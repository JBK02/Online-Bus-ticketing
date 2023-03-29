package application.Enum;

public enum UserLoginOptions {
    LOGIN("Login"),
    SIGN_UP("Signup"),
    BACK("Back"),
    QUIT("Quit");

    private final String description;

    UserLoginOptions(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }
}
