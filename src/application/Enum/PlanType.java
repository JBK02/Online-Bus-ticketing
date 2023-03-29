package application.Enum;

public enum PlanType {

    MONTHLY_PLAN("Monthly Plan"),
    STUDENT_PLAN("Student Plan"),
    CUSTOM_PLAN("Custom Plan");


    private final String description;

    PlanType(String description) {
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
