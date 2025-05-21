package Model;

import java.util.Date;

public class Vehicle {
    private int id;
    private String brand;
    private String model;
    private int year;
    private VehicleType vehicleType;
    private double dailyPrice;
    private double price;
    private boolean available;
    private boolean active;
    private Date createdAt;
    private Date updatedAt;
    private String plate;

    public Vehicle() {
    }

    public Vehicle(String brand, String model, int year, VehicleType vehicleType, 
                  double dailyPrice, double price, boolean available, String plate) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.vehicleType = vehicleType;
        this.dailyPrice = dailyPrice;
        this.price = price;
        this.available = available;
        this.active = true;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.plate = plate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(double dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    @Override
    public String toString() {
        return String.format("%-3d | %-10s | %-15s | %-4d | %-12s | %-10.2f | %-12.2f | %-8s | %-8s",
            id,
            brand,
            model,
            year,
            vehicleType.getDisplayName(),
            dailyPrice,
            price,
            available ? "Müsait" : "Kirada",
            plate);
    }
} 