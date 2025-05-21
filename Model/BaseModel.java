package Model;

import java.util.Date;

public abstract class BaseModel {
    private boolean isActive;
    private Date createdAt;
    private Date updatedAt;

    public BaseModel() {
        this.isActive = true;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        if (this.isActive != active) {
            this.isActive = active;
            this.updatedAt = new Date();
        }
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        if (this.createdAt == null) {
            this.createdAt = createdAt;
        }
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    protected void updateTimestamp() {
        this.updatedAt = new Date();
    }

    @Override
    public String toString() {
        return String.format("Durum: %s, Oluşturulma: %s, Güncellenme: %s",
            isActive ? "Aktif" : "Pasif",
            createdAt,
            updatedAt);
    }
} 