package Main;

import Db.DatabaseConnection;
import Daos.UserDao;
import Daos.VehicleDao;
import Daos.RentalDao;
import Service.UserService;
import Service.VehicleService;
import Service.RentalService;
import Model.User;
import Db.DefaultData;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
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

    public static void main(String[] args) {
        try {
            initializeServices();
            Scanner scanner = new Scanner(System.in);
            
            while (true) {
                if (currentUser == null) {
                    showMainMenu(scanner);
                } else {
                    if (currentUser.getRole().equals("ADMIN")) {
                        MainAdmin.initializeServices(vehicleService, userService, rentalService);
                        MainAdmin.showAdminMenu(scanner);
                    } else {
                        MainUser.userMenu(scanner);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Veritabanı bağlantı hatası: " + e.getMessage());
        }
    }

    private static void showMainMenu(Scanner scanner) {
        System.out.println("\n=== ANA MENÜ ===");
        System.out.println("1. Giriş Yap");
        System.out.println("2. Kayıt Ol");
        System.out.println("3. Varsayılan Verileri Yükle");
        System.out.println("4. Çıkış");
        System.out.print("Seçiminiz: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Buffer temizleme

        switch (choice) {
            case 1:
                MainUser.login(scanner);
                break;
            case 2:
                MainUser.register(scanner);
                break;
            case 3:
                System.out.println("\nVarsayılan veriler yükleniyor...");
                DefaultData.loadDefaultData(userService, vehicleService, rentalService);
                System.out.println("Varsayılan veriler başarıyla yüklendi.");
                break;
            case 4:
                System.out.println("Program sonlandırılıyor...");
                System.exit(0);
                break;
            default:
                System.out.println("Geçersiz seçim!");
        }
    }
} 