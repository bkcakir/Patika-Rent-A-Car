package Model;

public enum RentalPeriod {
    HOURLY("Saatlik"),
    DAILY("Günlük"),
    WEEKLY("Haftalık"),
    MONTHLY("Aylık");

    private final String displayName;

    RentalPeriod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 