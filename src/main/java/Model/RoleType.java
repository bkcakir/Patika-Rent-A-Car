package Model;

public enum RoleType {
    ADMIN("Admin"),
    USER("Kullanıcı"),
    CORPORATE_USER("Kurumsal Kullanıcı");

    private final String displayName;

    RoleType(String displayName) {
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