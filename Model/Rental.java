package Model;

import java.util.Date;

public class Rental extends BaseModel {
    private int id;
    private int userId;
    private int vehicleId;
    private Date startDate;
    private Date endDate;
    private double totalPrice;
    private double depositAmount;
    private String rentalType; // GUNLUK, AYLIK, YILLIK
    private boolean isActive;
    private User user;
    private Vehicle vehicle;
    private String remainingTime; // Kalan süre
    private int duration; // Kiralama süresi
    private double refundAmount; // İade tutarı

    public Rental(int userId, int vehicleId, Date startDate, Date endDate, 
                 double totalPrice, double depositAmount, String rentalType) {
        super();
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.depositAmount = depositAmount;
        this.rentalType = rentalType;
    }

    public Rental() {
        super();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    
    public double getDepositAmount() { return depositAmount; }
    public void setDepositAmount(double depositAmount) { this.depositAmount = depositAmount; }
    
    public String getRentalType() { return rentalType; }
    public void setRentalType(String rentalType) { this.rentalType = rentalType; }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public String getStatus() {
        return isActive ? "Aktif" : "Tamamlandı";
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(String remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }

    @Override
    public String toString() {
        // Tarihleri kısalt
        String shortStartDate = startDate.toString().substring(0, 10);
        String shortEndDate = endDate.toString().substring(0, 10);
        
        return String.format(
            "| %-3d | %-3d | %-3d | %-19s | %-19s | %-10.2f | %-10s | %-20s |\n",
            id,
            userId,
            vehicleId,
            shortStartDate,
            shortEndDate,
            totalPrice,
            isActive ? "Aktif" : "Tamamlandı",
            remainingTime != null ? remainingTime : "Hesaplanıyor..."
        );
    }
} 