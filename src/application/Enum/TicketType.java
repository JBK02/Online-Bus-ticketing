package application.Enum;

public enum TicketType {
    ONE_WAY("One way"),
    TWO_WAY("Two way"),
    ONE_DAY("One day");

    private final String description;

    TicketType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return this.description;
    }
}
