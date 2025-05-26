package Main;

import Db.DatabaseConnection;
import Daos.UserDao;
import Daos.VehicleDao;
import Daos.RentalDao;
import Service.UserService;
import Service.VehicleService;
import Service.RentalService;
import Model.User;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class MainUser {
    private static User currentUser = null;
    private static UserService userService;
    private static VehicleService vehicleService;
    private static RentalService rentalService;
    private static boolean initialized = false;

    private static void initializeServices() throws SQLException {
        if (initialized) return;
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection connection = db.getConnection();
        UserDao userDao = new UserDao(connection);
        VehicleDao vehicleDao = new VehicleDao(connection);
        RentalDao rentalDao = new RentalDao(connection);
        userService = new UserService(userDao);
        vehicleService = new VehicleService(vehicleDao);
        rentalService = new RentalService(rentalDao, vehicleDao);
        initialized = true;
    }

    public static void login(Scanner scanner) {
        try {
            initializeServices();
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Şifre: ");
            String password = scanner.nextLine();
            currentUser = userService.login(email, password);
            if (currentUser != null) {
                System.out.println("Giriş başarılı!");
                if (currentUser.getRole().equals("ADMIN")) {
                    MainAdmin.initializeServices(vehicleService, userService, rentalService);
                    MainAdmin.showAdminMenu(scanner);
                } else {
                    userMenu(scanner);
                }
            } else {
                System.out.println("Geçersiz email veya şifre!");
            }
        } catch (SQLException e) {
            System.err.println("Giriş hatası: " + e.getMessage());
        }
    }

    public static void register(Scanner scanner) {
        try {
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Şifre: ");
            String password = scanner.nextLine();
            System.out.print("Ad: ");
            String name = scanner.nextLine();
            System.out.print("Soyad: ");
            String lastName = scanner.nextLine();
            System.out.print("Yaş: ");
            int age = scanner.nextInt();
            scanner.nextLine(); // Buffer temizleme
            
            System.out.println("Kurumsal müşteri misiniz? (E/H): ");
            String isCorporateStr = scanner.nextLine();
            boolean isCorporate = isCorporateStr.equalsIgnoreCase("E");
            
            User user = userService.register(email, password, "MUSTERI", age, isCorporate, name, lastName);
            System.out.println("Kayıt başarılı! Hoş geldiniz, " + user.getName());
        } catch (SQLException e) {
            System.err.println("Kayıt hatası: " + e.getMessage());
        }
    }

    public static void userMenu(Scanner scanner) {
        try {
            initializeServices();
            while (true) {
                System.out.println("\n=== KULLANICI MENÜSÜ ===");
                System.out.println("1. Araç Kirala");
                System.out.println("2. Kiralama Geçmişi");
                System.out.println("3. Aktif Kiralamalar");
                System.out.println("4. Araç Teslim");
                System.out.println("5. Çıkış Yap");
                System.out.print("Seçiminiz: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Buffer temizleme

                switch (choice) {
                    case 1:
                        MainRental.rentVehicle(scanner, currentUser, rentalService, vehicleService);
                        break;
                    case 2:
                        MainRental.listUserRentals(scanner, rentalService, currentUser);
                        break;
                    case 3:
                        MainRental.listActiveRentals(scanner, rentalService, currentUser);
                        break;
                    case 4:
                        MainRental.returnVehicle(scanner, rentalService, currentUser);
                        break;
                    case 5:
                        currentUser = null;
                        return;
                    default:
                        System.out.println("Geçersiz seçim!");
                }
            }
        } catch (SQLException e) {
            System.err.println("Menü hatası: " + e.getMessage());
        }
    }
} 