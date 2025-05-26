package Main;

import Service.VehicleService;
import Model.Vehicle;
import Model.VehicleType;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class MainVehicle {
    static int Count = 1;
    public static void listVehicles(Scanner scanner, VehicleService vehicleService) {

        try {
            List<Vehicle> vehicles = vehicleService.getAllVehicles();
            System.out.println("\nID  | Marka      | Model            | Yıl  | Kategori      | Günlük    | Fiyat        | Durum    | Plaka");
            System.out.println("----|------------|------------------|------|---------------|-----------|--------------|----------|--------");
            for (Vehicle vehicle : vehicles) {
                System.out.println(String.format("%-3d | %-10s | %-15s | %-4d | %-12s | %-10.2f | %-12.2f | %-8s | %-8s",
                    Count,
                    vehicle.getBrand(),
                    vehicle.getModel(),
                    vehicle.getYear(),
                    vehicle.getVehicleType().getDisplayName(),
                    vehicle.getDailyPrice(),
                    vehicle.getPrice(),
                    vehicle.isAvailable() ? "Müsait" : "Kirada",
                    vehicle.getPlate()));
                Count++;
            }
            System.out.println();
        } catch (SQLException e) {
            System.err.println("Araç listeleme hatası: " + e.getMessage());
        }
    }

    public static void addVehicle(Scanner scanner, VehicleService vehicleService) {
        try {
            System.out.print("Marka: ");
            String brand = scanner.nextLine();
            System.out.print("Model: ");
            String model = scanner.nextLine();
            System.out.print("Yıl: ");
            int year = scanner.nextInt();
            scanner.nextLine(); // Buffer temizleme
            
            System.out.println("Araç tipi seçin:");
            System.out.println("1. SEDAN");
            System.out.println("2. SUV");
            System.out.println("3. HATCHBACK");
            System.out.println("4. STATION WAGON");
            System.out.println("5. SPOR");
            System.out.print("Seçiminiz (1-5): ");
            int typeChoice = scanner.nextInt();
            scanner.nextLine(); // Buffer temizleme
            
            VehicleType vehicleType;
            switch (typeChoice) {
                case 1:
                    vehicleType = VehicleType.SEDAN;
                    break;
                case 2:
                    vehicleType = VehicleType.SUV;
                    break;
                case 3:
                    vehicleType = VehicleType.HATCHBACK;
                    break;
                case 4:
                    vehicleType = VehicleType.STATION_WAGON;
                    break;
                case 5:
                    vehicleType = VehicleType.SPOR;
                    break;
                default:
                    System.out.println("Geçersiz seçim! Varsayılan olarak SEDAN seçildi.");
                    vehicleType = VehicleType.SEDAN;
            }

            System.out.print("Günlük fiyat: ");
            double dailyPrice = scanner.nextDouble();
            System.out.print("Araç bedeli: ");
            double price = scanner.nextDouble();
            scanner.nextLine(); // Buffer temizleme
            
            System.out.print("Plaka: ");
            String plate = scanner.nextLine();
            
            Vehicle vehicle = new Vehicle(brand, model, year, vehicleType, dailyPrice, price, true, plate);
            vehicleService.addVehicle(vehicle);
            System.out.println("Araç başarıyla eklendi!");
        } catch (SQLException e) {
            System.err.println("Araç ekleme hatası: " + e.getMessage());
        }
    }

    public static void searchByVehicleType(Scanner scanner, VehicleService vehicleService) {
        try {
            System.out.println("Araç tipi seçin:");
            System.out.println("1. SEDAN");
            System.out.println("2. SUV");
            System.out.println("3. HATCHBACK");
            System.out.println("4. STATION WAGON");
            System.out.println("5. SPOR");
            System.out.print("Seçiminiz (1-5): ");
            int typeChoice = scanner.nextInt();
            scanner.nextLine(); // Buffer temizleme
            
            VehicleType vehicleType;
            switch (typeChoice) {
                case 1:
                    vehicleType = VehicleType.SEDAN;
                    break;
                case 2:
                    vehicleType = VehicleType.SUV;
                    break;
                case 3:
                    vehicleType = VehicleType.HATCHBACK;
                    break;
                case 4:
                    vehicleType = VehicleType.STATION_WAGON;
                    break;
                case 5:
                    vehicleType = VehicleType.SPOR;
                    break;
                default:
                    System.out.println("Geçersiz seçim! Varsayılan olarak SEDAN seçildi.");
                    vehicleType = VehicleType.SEDAN;
            }

            List<Vehicle> vehicles = vehicleService.getVehiclesByCategory(vehicleType);
            if (vehicles.isEmpty()) {
                System.out.println("Bu tipte araç bulunamadı!");
            } else {
                System.out.println("\n" + vehicleType.getDisplayName() + " tipindeki araçlar:");
                System.out.println("ID  | Marka      | Model            | Yıl  | Kategori      | Günlük    | Fiyat        | Durum    | Plaka");
                System.out.println("----|------------|------------------|------|---------------|-----------|--------------|----------|--------");
                for (Vehicle vehicle : vehicles) {
                    System.out.println(vehicle);
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.err.println("Araç tipi arama hatası: " + e.getMessage());
        }
    }

    public static void listAvailableVehicles(Scanner scanner, VehicleService vehicleService) {
        Count = 1;
        try {
            List<Vehicle> vehicles = vehicleService.getAvailableVehicles();
            if (vehicles.isEmpty()) {
                System.out.println("\nMüsait araç bulunmamaktadır!");
            } else {
                System.out.println("\nMüsait Araçlar:");
                System.out.println("ID  | Marka      | Model            | Yıl  | Kategori      | Günlük    | Fiyat        | Plaka");
                System.out.println("----|------------|------------------|------|---------------|-----------|--------------|--------");
                for (Vehicle vehicle : vehicles) {
                    System.out.println(String.format("%-3d | %-10s | %-15s | %-4d | %-12s | %-10.2f | %-12.2f | %-8s",
                            Count,
                            vehicle.getBrand(),
                            vehicle.getModel(),
                            vehicle.getYear(),
                            vehicle.getVehicleType().getDisplayName(),
                            vehicle.getDailyPrice(),
                            vehicle.getPrice(),
                            vehicle.getPlate()));
                    Count++;
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.err.println("Müsait araç listeleme hatası: " + e.getMessage());
        }
    }
} 