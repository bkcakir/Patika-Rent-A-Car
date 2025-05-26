package Db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import Model.User;
import Model.Vehicle;
import Model.VehicleType;
import Service.UserService;
import Service.VehicleService;
import Service.RentalService;
import Daos.UserDao;
import Daos.VehicleDao;
import Daos.RentalDao;
import Helper.PasswordHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Date;
import java.util.Calendar;

public class DefaultData {
    private static final class Constants {
        private static final String ADMIN_PASSWORD = "Admin123";
        private static final String USER_PASSWORD = "User123";
        private static final String CORPORATE_PASSWORD = "User456";
        
        private static final String ADMIN_EMAIL = "admin@rentacar.com";
        private static final String USER1_EMAIL = "user1@rentacar.com";
        private static final String USER2_EMAIL = "user2@rentacar.com";
        private static final String CORPORATE1_EMAIL = "corporate1@rentacar.com";
        private static final String CORPORATE2_EMAIL = "corporate2@rentacar.com";
        
        private static final String ADMIN_ROLE = "ADMIN";
        private static final String CUSTOMER_ROLE = "MUSTERI";
        private static final String CORPORATE_ROLE = "KURUMSAL";
        
        private static final String RENTAL_TYPE_DAILY = "GUNLUK";
    }
    
    public static void loadDefaultData(UserService userService, VehicleService vehicleService, RentalService rentalService) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getInstance().getConnection();
            Statement statement = connection.createStatement();
            
            // Önce tüm tablolardaki verileri sil
            clearExistingData(statement);
            
            // Kullanıcıları ekle
            addSampleUsers(userService);
            
            // Araçları ekle
            addSampleVehicles(vehicleService);
            
            // Kiralama kayıtlarını ekle
            addSampleRentals(userService, vehicleService, rentalService);
            
            System.out.println("Varsayılan veriler başarıyla yüklendi.");
        } catch (SQLException e) {
            System.err.println("Varsayılan veriler yüklenirken hata oluştu: " + e.getMessage());
        }
    }
    
    private static void addUser(UserService userService, String email, String password, 
                              String role, int age, boolean isCorporate, String name, String lastName) throws SQLException {
        userService.register(email, password, role, age, isCorporate, name, lastName);
    }
    
    private static void addVehicle(VehicleService vehicleService, String brand, String model, int year,
                                 VehicleType vehicleType, double dailyPrice, double price, String plate) throws SQLException {
        Vehicle vehicle = new Vehicle(brand, model, year, vehicleType, dailyPrice, price, true, plate);
        vehicleService.addVehicle(vehicle);
    }
    
    private static void clearExistingData(Statement statement) throws SQLException {
        try {
            statement.execute("DELETE FROM rentals");
            System.out.println("Kiralama tablosu temizlendi.");
            statement.execute("DELETE FROM vehicles");
            System.out.println("Araçlar tablosu temizlendi.");
            statement.execute("DELETE FROM users");
            System.out.println("Kullanıcı tablosu temizlendi.");
            statement.execute("ALTER SEQUENCE users_id_seq RESTART WITH 1");
            statement.execute("ALTER SEQUENCE vehicles_id_seq RESTART WITH 1");
            statement.execute("ALTER SEQUENCE rentals_id_seq RESTART WITH 1");
        } catch (SQLException e) {
            System.err.println("Veri temizleme sırasında hata: " + e.getMessage());
            throw e;
        }
    }
    
    private static void loadDataFromFile(Statement statement) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("Db/insert_data.sql"));
        StringBuilder sql = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            // Yorum satırlarını atla
            if (line.trim().startsWith("--")) {
                continue;
            }
            
            sql.append(line);
            if (line.trim().endsWith(";")) {
                statement.execute(sql.toString());
                sql = new StringBuilder();
            }
        }
        
        reader.close();
    }
    
    public static boolean isDataLoaded() {
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             Statement statement = connection.createStatement()) {
            
            var result = statement.executeQuery("SELECT COUNT(*) FROM users");
            if (result.next()) {
                return result.getInt(1) > 0;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Veri kontrolü hatası: " + e.getMessage());
            return false;
        }
    }

    private static void addSampleUsers(UserService userService) throws SQLException {
        // Admin kullanıcıları
        addUser(userService, "admin1@rentacar.com", Constants.ADMIN_PASSWORD, 
               Constants.ADMIN_ROLE, 35, false, "Ahmet", "Yılmaz");
        addUser(userService, "admin2@rentacar.com", Constants.ADMIN_PASSWORD, 
               Constants.ADMIN_ROLE, 32, false, "Mehmet", "Kaya");
        
        // Normal kullanıcılar
        addUser(userService, "ahmet.yilmaz@email.com", Constants.USER_PASSWORD, 
               Constants.CUSTOMER_ROLE, 28, false, "Ahmet", "Yılmaz");
        addUser(userService, "ayse.demir@email.com", Constants.USER_PASSWORD, 
               Constants.CUSTOMER_ROLE, 25, false, "Ayşe", "Demir");
        addUser(userService, "mehmet.kaya@email.com", Constants.USER_PASSWORD, 
               Constants.CUSTOMER_ROLE, 32, false, "Mehmet", "Kaya");
        addUser(userService, "fatma.celik@email.com", Constants.USER_PASSWORD, 
               Constants.CUSTOMER_ROLE, 27, false, "Fatma", "Çelik");
        addUser(userService, "ali.yildiz@email.com", Constants.USER_PASSWORD, 
               Constants.CUSTOMER_ROLE, 35, false, "Ali", "Yıldız");
        addUser(userService, "zeynep.arslan@email.com", Constants.USER_PASSWORD, 
               Constants.CUSTOMER_ROLE, 29, false, "Zeynep", "Arslan");
        addUser(userService, "mustafa.ozturk@email.com", Constants.USER_PASSWORD, 
               Constants.CUSTOMER_ROLE, 31, false, "Mustafa", "Öztürk");
        addUser(userService, "elif.kurt@email.com", Constants.USER_PASSWORD, 
               Constants.CUSTOMER_ROLE, 26, false, "Elif", "Kurt");
        addUser(userService, "burak.aydin@email.com", Constants.USER_PASSWORD, 
               Constants.CUSTOMER_ROLE, 33, false, "Burak", "Aydın");
        addUser(userService, "seda.sahin@email.com", Constants.USER_PASSWORD, 
               Constants.CUSTOMER_ROLE, 30, false, "Seda", "Şahin");
        
        // Kurumsal kullanıcılar
        addUser(userService, "info@abc-ltd.com", Constants.CORPORATE_PASSWORD, 
               Constants.CORPORATE_ROLE, 40, true, "Mustafa", "Öztürk");
        addUser(userService, "rental@xyz-corp.com", Constants.CORPORATE_PASSWORD, 
               Constants.CORPORATE_ROLE, 45, true, "Zeynep", "Arslan");
        addUser(userService, "fleet@global-transport.com", Constants.CORPORATE_PASSWORD, 
               Constants.CORPORATE_ROLE, 38, true, "Emre", "Şahin");
        addUser(userService, "operations@mega-logistics.com", Constants.CORPORATE_PASSWORD, 
               Constants.CORPORATE_ROLE, 42, true, "Elif", "Kurt");
        addUser(userService, "fleet@express-delivery.com", Constants.CORPORATE_PASSWORD, 
               Constants.CORPORATE_ROLE, 36, true, "Burak", "Aydın");
        addUser(userService, "info@tech-solutions.com", Constants.CORPORATE_PASSWORD, 
               Constants.CORPORATE_ROLE, 41, true, "Ahmet", "Yıldız");
        addUser(userService, "fleet@logistics-pro.com", Constants.CORPORATE_PASSWORD, 
               Constants.CORPORATE_ROLE, 39, true, "Mehmet", "Demir");
        addUser(userService, "operations@transport-plus.com", Constants.CORPORATE_PASSWORD, 
               Constants.CORPORATE_ROLE, 43, true, "Ayşe", "Çelik");
        addUser(userService, "fleet@delivery-express.com", Constants.CORPORATE_PASSWORD, 
               Constants.CORPORATE_ROLE, 37, true, "Fatma", "Kaya");
        addUser(userService, "info@corporate-rent.com", Constants.CORPORATE_PASSWORD, 
               Constants.CORPORATE_ROLE, 44, true, "Ali", "Öztürk");
    }

    private static void addSampleVehicles(VehicleService vehicleService) throws SQLException {
        System.out.println("Örnek araçlar ekleniyor...");
        
        // Sedan araçlar (15 adet)
        vehicleService.addVehicle(new Vehicle("Toyota", "Corolla", 2022, VehicleType.SEDAN, 1200, 850000, true, "34ABC123"));
        vehicleService.addVehicle(new Vehicle("Honda", "Civic", 2021, VehicleType.SEDAN, 1300, 900000, true, "34DEF456"));
        vehicleService.addVehicle(new Vehicle("Volkswagen", "Passat", 2023, VehicleType.SEDAN, 1500, 1200000, true, "34GHI789"));
        vehicleService.addVehicle(new Vehicle("Mercedes", "C200", 2022, VehicleType.SEDAN, 1800, 1500000, true, "34JKL012"));
        vehicleService.addVehicle(new Vehicle("BMW", "320i", 2021, VehicleType.SEDAN, 1700, 1400000, true, "34MNO345"));
        vehicleService.addVehicle(new Vehicle("Audi", "A4", 2023, VehicleType.SEDAN, 1600, 1300000, true, "34PRS678"));
        vehicleService.addVehicle(new Vehicle("Hyundai", "Elantra", 2022, VehicleType.SEDAN, 1100, 800000, true, "34TUV901"));
        vehicleService.addVehicle(new Vehicle("Kia", "Cerato", 2021, VehicleType.SEDAN, 1000, 750000, true, "34WXY234"));
        vehicleService.addVehicle(new Vehicle("Renault", "Megane", 2023, VehicleType.SEDAN, 1050, 780000, true, "34ZAB567"));
        vehicleService.addVehicle(new Vehicle("Ford", "Focus", 2022, VehicleType.SEDAN, 1150, 820000, true, "34CDE890"));
        vehicleService.addVehicle(new Vehicle("Peugeot", "308", 2021, VehicleType.SEDAN, 1250, 880000, true, "34FGH123"));
        vehicleService.addVehicle(new Vehicle("Citroen", "C4", 2023, VehicleType.SEDAN, 1350, 920000, true, "34IJK456"));
        vehicleService.addVehicle(new Vehicle("Skoda", "Octavia", 2022, VehicleType.SEDAN, 1400, 950000, true, "34LMN789"));
        vehicleService.addVehicle(new Vehicle("Volvo", "S60", 2021, VehicleType.SEDAN, 1900, 1600000, true, "34OPQ012"));
        vehicleService.addVehicle(new Vehicle("Lexus", "ES", 2023, VehicleType.SEDAN, 2000, 1700000, true, "34RST345"));
        
        // SUV araçlar (15 adet)
        vehicleService.addVehicle(new Vehicle("Toyota", "RAV4", 2022, VehicleType.SUV, 1800, 1500000, true, "34UVW678"));
        vehicleService.addVehicle(new Vehicle("Honda", "CR-V", 2021, VehicleType.SUV, 1700, 1400000, true, "34XYZ901"));
        vehicleService.addVehicle(new Vehicle("Volkswagen", "Tiguan", 2023, VehicleType.SUV, 1900, 1600000, true, "34ABC234"));
        vehicleService.addVehicle(new Vehicle("Mercedes", "GLC", 2022, VehicleType.SUV, 2200, 1800000, true, "34DEF567"));
        vehicleService.addVehicle(new Vehicle("BMW", "X3", 2021, VehicleType.SUV, 2100, 1700000, true, "34GHI890"));
        vehicleService.addVehicle(new Vehicle("Audi", "Q5", 2023, VehicleType.SUV, 2000, 1650000, true, "34JKL123"));
        vehicleService.addVehicle(new Vehicle("Hyundai", "Tucson", 2022, VehicleType.SUV, 1600, 1300000, true, "34MNO456"));
        vehicleService.addVehicle(new Vehicle("Kia", "Sportage", 2021, VehicleType.SUV, 1500, 1250000, true, "34PRS789"));
        vehicleService.addVehicle(new Vehicle("Renault", "Kadjar", 2023, VehicleType.SUV, 1550, 1280000, true, "34TUV012"));
        vehicleService.addVehicle(new Vehicle("Ford", "Kuga", 2022, VehicleType.SUV, 1650, 1350000, true, "34WXY345"));
        vehicleService.addVehicle(new Vehicle("Peugeot", "3008", 2021, VehicleType.SUV, 1750, 1450000, true, "34ZAB678"));
        vehicleService.addVehicle(new Vehicle("Citroen", "C5 Aircross", 2023, VehicleType.SUV, 1850, 1550000, true, "34CDE901"));
        vehicleService.addVehicle(new Vehicle("Skoda", "Kodiaq", 2022, VehicleType.SUV, 1950, 1580000, true, "34FGH234"));
        vehicleService.addVehicle(new Vehicle("Volvo", "XC60", 2021, VehicleType.SUV, 2300, 1900000, true, "34IJK567"));
        vehicleService.addVehicle(new Vehicle("Lexus", "NX", 2023, VehicleType.SUV, 2400, 2000000, true, "34LMN890"));
        
        // Hatchback araçlar (10 adet)
        vehicleService.addVehicle(new Vehicle("Volkswagen", "Golf", 2022, VehicleType.HATCHBACK, 1100, 800000, true, "34OPQ123"));
        vehicleService.addVehicle(new Vehicle("Ford", "Focus", 2021, VehicleType.HATCHBACK, 1000, 750000, true, "34RST456"));
        vehicleService.addVehicle(new Vehicle("Renault", "Megane", 2023, VehicleType.HATCHBACK, 1050, 780000, true, "34UVW789"));
        vehicleService.addVehicle(new Vehicle("Peugeot", "308", 2022, VehicleType.HATCHBACK, 1150, 820000, true, "34XYZ012"));
        vehicleService.addVehicle(new Vehicle("Citroen", "C4", 2021, VehicleType.HATCHBACK, 1200, 850000, true, "34ABC345"));
        vehicleService.addVehicle(new Vehicle("Skoda", "Octavia", 2023, VehicleType.HATCHBACK, 1250, 880000, true, "34DEF678"));
        vehicleService.addVehicle(new Vehicle("Hyundai", "i30", 2022, VehicleType.HATCHBACK, 950, 700000, true, "34GHI901"));
        vehicleService.addVehicle(new Vehicle("Kia", "Ceed", 2021, VehicleType.HATCHBACK, 900, 680000, true, "34JKL234"));
        vehicleService.addVehicle(new Vehicle("Toyota", "Corolla", 2023, VehicleType.HATCHBACK, 1300, 900000, true, "34MNO567"));
        vehicleService.addVehicle(new Vehicle("Honda", "Civic", 2022, VehicleType.HATCHBACK, 1350, 920000, true, "34PRS890"));
        
        // Station Wagon araçlar (5 adet)
        vehicleService.addVehicle(new Vehicle("Volkswagen", "Passat Variant", 2022, VehicleType.STATION_WAGON, 1600, 1300000, true, "34TUV123"));
        vehicleService.addVehicle(new Vehicle("Skoda", "Superb", 2021, VehicleType.STATION_WAGON, 1550, 1250000, true, "34WXY456"));
        vehicleService.addVehicle(new Vehicle("Mercedes", "C-Class Estate", 2023, VehicleType.STATION_WAGON, 2000, 1800000, true, "34ZAB789"));
        vehicleService.addVehicle(new Vehicle("BMW", "3 Series Touring", 2022, VehicleType.STATION_WAGON, 1900, 1700000, true, "34CDE012"));
        vehicleService.addVehicle(new Vehicle("Audi", "A4 Avant", 2021, VehicleType.STATION_WAGON, 1800, 1600000, true, "34FGH345"));
        
        // Spor araçlar (5 adet)
        vehicleService.addVehicle(new Vehicle("BMW", "M3", 2022, VehicleType.SPOR, 2500, 2500000, true, "34IJK678"));
        vehicleService.addVehicle(new Vehicle("Mercedes", "AMG C63", 2021, VehicleType.SPOR, 2600, 2600000, true, "34LMN901"));
        vehicleService.addVehicle(new Vehicle("Audi", "RS5", 2023, VehicleType.SPOR, 2400, 2400000, true, "34OPQ234"));
        vehicleService.addVehicle(new Vehicle("Porsche", "911", 2022, VehicleType.SPOR, 3000, 3000000, true, "34RST567"));
        vehicleService.addVehicle(new Vehicle("Jaguar", "F-Type", 2021, VehicleType.SPOR, 2800, 2800000, true, "34UVW890"));
        
        System.out.println("Örnek araçlar başarıyla eklendi!");
    }

    private static void addSampleRentals(UserService userService, VehicleService vehicleService, RentalService rentalService) throws SQLException {
        // Tüm kullanıcıları ve araçları al
        List<User> users = userService.getAllUsers();
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        
        if (users.isEmpty() || vehicles.isEmpty()) {
            System.err.println("Kullanıcı veya araç bulunamadı. Kiralama verileri yüklenemedi.");
            return;
        }
        
        String[] rentalTypes = {"SAATLIK", "GUNLUK", "HAFTALIK", "AYLIK"};
        
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.MARCH, 1); // Başlangıç tarihi: 1 Mart 2024
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        
        int rentalCount = 0;
        int maxAttempts = 100;
        
        while (rentalCount < 20 && maxAttempts > 0) {
            User user = users.get((int) (Math.random() * users.size()));
            Vehicle vehicle = vehicles.get((int) (Math.random() * vehicles.size()));
            
            if (!vehicle.isAvailable()) {
                maxAttempts--;
                continue;
            }
            
            if (vehicle.getPrice() > 2000000 && user.getAge() <= 30) {
                maxAttempts--;
                continue;
            }
            
            String rentalType = user.isCorporate() ? "AYLIK" : rentalTypes[(int) (Math.random() * rentalTypes.length)];
            
            // Başlangıç tarihi
            Date startDate = calendar.getTime();
            
            // Süre belirleme
            int duration = 0;
            switch (rentalType) {
                case "SAATLIK":
                    duration = (int) (Math.random() * 24) + 1; // 1-24 saat
                    break;
                case "GUNLUK":
                    duration = (int) (Math.random() * 7) + 1; // 1-7 gün
                    break;
                case "HAFTALIK":
                    duration = (int) (Math.random() * 4) + 1; // 1-4 hafta
                    break;
                case "AYLIK":
                    duration = (int) (Math.random() * 12) + 1; // 1-12 ay
                    break;
            }
            
            // Fiyat hesaplama
            double basePrice = vehicle.getDailyPrice() * getRentalDuration(rentalType, duration);
            double totalPrice = calculateTotalPrice(basePrice, rentalType);
            
            // Depozito hesaplama (2 milyon TL üzeri araçlar için %10)
            double depositAmount = vehicle.getPrice() > 2000000 ? vehicle.getPrice() * 0.1 : 0;
            
            try {
                rentalService.createRental(user, vehicle.getId(), startDate, duration, totalPrice, depositAmount, rentalType);
                rentalCount++;
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            } catch (SQLException e) {
                maxAttempts--;
                continue;
            }
        }
        
        if (rentalCount == 0) {
            System.err.println("Hiç kiralama kaydı oluşturulamadı.");
        } else {
            System.out.println(rentalCount + " adet kiralama kaydı oluşturuldu.");
        }
    }
    
    private static int getRentalDuration(String rentalType, int duration) {
        switch (rentalType) {
            case "SAATLIK": return duration;
            case "GUNLUK": return duration;
            case "HAFTALIK": return duration * 7;
            case "AYLIK": return duration * 30;
            default: return 1;
        }
    }
    
    private static double calculateTotalPrice(double basePrice, String rentalType) {
        switch (rentalType) {
            case "SAATLIK": return basePrice * 0.1;
            case "HAFTALIK": return basePrice * 0.8;
            case "AYLIK": return basePrice * 0.7;
            default: return basePrice;
        }
    }

    private static void insertDefaultUsers(UserDao userDao) throws SQLException {
        // Admin kullanıcısı
        User admin = new User("admin@rentacar.com", "admin123", "ADMIN", 30, false, "Ahmet", "Yılmaz");
        userDao.save(admin);

        // Örnek müşteri
        User customer = new User("musteri@rentacar.com", "musteri123", "MUSTERI", 25, false, "Mehmet", "Demir");
        userDao.save(customer);

        // Örnek kurumsal müşteri
        User corporate = new User("kurumsal@rentacar.com", "kurumsal123", "KURUMSAL", 35, true, "Mustafa", "Öztürk");
        userDao.save(corporate);
    }

    public static void addDefaultData(UserService userService, VehicleService vehicleService, RentalService rentalService) {
        try {
            // Varsayılan kullanıcılar
            userService.register("admin@rentacar.com", "admin123", "ADMIN", 30, false, "Ahmet", "Yılmaz");
            userService.register("musteri@rentacar.com", "musteri123", "MUSTERI", 25, false, "Mehmet", "Demir");
            userService.register("kurumsal@rentacar.com", "kurumsal123", "KURUMSAL", 35, true, "Mustafa", "Öztürk");
            userService.register("musteri2@rentacar.com", "musteri123", "MUSTERI", 28, false, "Ayşe", "Kaya");
            userService.register("kurumsal2@rentacar.com", "kurumsal123", "KURUMSAL", 40, true, "Zeynep", "Şahin");
            userService.register("musteri3@rentacar.com", "musteri123", "MUSTERI", 32, false, "Ali", "Çelik");
            userService.register("kurumsal3@rentacar.com", "kurumsal123", "KURUMSAL", 38, true, "Fatma", "Yıldız");
            userService.register("musteri4@rentacar.com", "musteri123", "MUSTERI", 27, false, "Emre", "Arslan");
            userService.register("kurumsal4@rentacar.com", "kurumsal123", "KURUMSAL", 42, true, "Elif", "Kurt");
            userService.register("musteri5@rentacar.com", "musteri123", "MUSTERI", 30, false, "Burak", "Aydın");

            // Varsayılan araçlar
            Vehicle sedan = new Vehicle("Toyota", "Corolla", 2023, VehicleType.SEDAN, 500.0, 500000.0, true, "34ABC123");
            Vehicle suv = new Vehicle("Honda", "CR-V", 2023, VehicleType.SUV, 700.0, 700000.0, true, "34DEF456");
            Vehicle hatchback = new Vehicle("Volkswagen", "Golf", 2023, VehicleType.HATCHBACK, 450.0, 450000.0, true, "34GHI789");
            Vehicle stationWagon = new Vehicle("BMW", "3 Series Touring", 2023, VehicleType.STATION_WAGON, 800.0, 800000.0, true, "34JKL012");
            Vehicle sport = new Vehicle("Porsche", "911", 2023, VehicleType.SPOR, 1500.0, 1500000.0, true, "34MNO345");
            Vehicle sedan2 = new Vehicle("Mercedes", "C200", 2023, VehicleType.SEDAN, 600.0, 600000.0, true, "34PQR678");
            Vehicle suv2 = new Vehicle("BMW", "X5", 2023, VehicleType.SUV, 900.0, 900000.0, true, "34STU901");
            Vehicle hatchback2 = new Vehicle("Renault", "Megane", 2023, VehicleType.HATCHBACK, 400.0, 400000.0, true, "34VWX234");
            Vehicle stationWagon2 = new Vehicle("Volvo", "V60", 2023, VehicleType.STATION_WAGON, 750.0, 750000.0, true, "34YZA567");
            Vehicle sport2 = new Vehicle("Audi", "RS6", 2023, VehicleType.SPOR, 1200.0, 1200000.0, true, "34BCD890");
            
            vehicleService.addVehicle(sedan);
            vehicleService.addVehicle(suv);
            vehicleService.addVehicle(hatchback);
            vehicleService.addVehicle(stationWagon);
            vehicleService.addVehicle(sport);
            vehicleService.addVehicle(sedan2);
            vehicleService.addVehicle(suv2);
            vehicleService.addVehicle(hatchback2);
            vehicleService.addVehicle(stationWagon2);
            vehicleService.addVehicle(sport2);

            // Kullanıcıları al
            User musteri1 = userService.login("musteri@rentacar.com", "musteri123");
            User kurumsal1 = userService.login("kurumsal@rentacar.com", "kurumsal123");
            User musteri2 = userService.login("musteri2@rentacar.com", "musteri123");
            User kurumsal2 = userService.login("kurumsal2@rentacar.com", "kurumsal123");
            User musteri3 = userService.login("musteri3@rentacar.com", "musteri123");
            User kurumsal3 = userService.login("kurumsal3@rentacar.com", "kurumsal123");
            User musteri4 = userService.login("musteri4@rentacar.com", "musteri123");
            User kurumsal4 = userService.login("kurumsal4@rentacar.com", "kurumsal123");
            User musteri5 = userService.login("musteri5@rentacar.com", "musteri123");

            // Varsayılan aktif kiralamalar
            Calendar calendar = Calendar.getInstance();
            calendar.set(2024, Calendar.MARCH, 1); // Başlangıç tarihi: 1 Mart 2024
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Date currentDate = calendar.getTime();

            // 1-10. Kiralamalar - Müşteri 1
            rentalService.createRental(musteri1, sedan.getId(), currentDate, 5, 2500.0, 500.0, "GUNLUK");
            rentalService.createRental(musteri1, suv.getId(), currentDate, 3, 2100.0, 700.0, "GUNLUK");
            rentalService.createRental(musteri1, hatchback.getId(), currentDate, 2, 900.0, 450.0, "GUNLUK");
            rentalService.createRental(musteri1, stationWagon.getId(), currentDate, 4, 3200.0, 800.0, "GUNLUK");
            rentalService.createRental(musteri1, sport.getId(), currentDate, 1, 1500.0, 1500.0, "GUNLUK");
            rentalService.createRental(musteri1, sedan2.getId(), currentDate, 3, 1800.0, 600.0, "GUNLUK");
            rentalService.createRental(musteri1, suv2.getId(), currentDate, 2, 1800.0, 900.0, "GUNLUK");
            rentalService.createRental(musteri1, hatchback2.getId(), currentDate, 4, 1600.0, 400.0, "GUNLUK");
            rentalService.createRental(musteri1, stationWagon2.getId(), currentDate, 5, 3750.0, 750.0, "GUNLUK");
            rentalService.createRental(musteri1, sport2.getId(), currentDate, 2, 2400.0, 1200.0, "GUNLUK");

            // 11-20. Kiralamalar - Kurumsal 1
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            currentDate = calendar.getTime();
            rentalService.createRental(kurumsal1, sedan.getId(), currentDate, 7, 3500.0, 500.0, "GUNLUK");
            rentalService.createRental(kurumsal1, suv.getId(), currentDate, 5, 3500.0, 700.0, "GUNLUK");
            rentalService.createRental(kurumsal1, hatchback.getId(), currentDate, 3, 1350.0, 450.0, "GUNLUK");
            rentalService.createRental(kurumsal1, stationWagon.getId(), currentDate, 6, 4800.0, 800.0, "GUNLUK");
            rentalService.createRental(kurumsal1, sport.getId(), currentDate, 2, 3000.0, 1500.0, "GUNLUK");
            rentalService.createRental(kurumsal1, sedan2.getId(), currentDate, 4, 2400.0, 600.0, "GUNLUK");
            rentalService.createRental(kurumsal1, suv2.getId(), currentDate, 3, 2700.0, 900.0, "GUNLUK");
            rentalService.createRental(kurumsal1, hatchback2.getId(), currentDate, 5, 2000.0, 400.0, "GUNLUK");
            rentalService.createRental(kurumsal1, stationWagon2.getId(), currentDate, 7, 5250.0, 750.0, "GUNLUK");
            rentalService.createRental(kurumsal1, sport2.getId(), currentDate, 3, 3600.0, 1200.0, "GUNLUK");

            // 21-30. Kiralamalar - Müşteri 2
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            currentDate = calendar.getTime();
            rentalService.createRental(musteri2, sedan.getId(), currentDate, 4, 2000.0, 500.0, "GUNLUK");
            rentalService.createRental(musteri2, suv.getId(), currentDate, 2, 1400.0, 700.0, "GUNLUK");
            rentalService.createRental(musteri2, hatchback.getId(), currentDate, 3, 1350.0, 450.0, "GUNLUK");
            rentalService.createRental(musteri2, stationWagon.getId(), currentDate, 5, 4000.0, 800.0, "GUNLUK");
            rentalService.createRental(musteri2, sport.getId(), currentDate, 1, 1500.0, 1500.0, "GUNLUK");
            rentalService.createRental(musteri2, sedan2.getId(), currentDate, 2, 1200.0, 600.0, "GUNLUK");
            rentalService.createRental(musteri2, suv2.getId(), currentDate, 4, 3600.0, 900.0, "GUNLUK");
            rentalService.createRental(musteri2, hatchback2.getId(), currentDate, 3, 1200.0, 400.0, "GUNLUK");
            rentalService.createRental(musteri2, stationWagon2.getId(), currentDate, 6, 4500.0, 750.0, "GUNLUK");
            rentalService.createRental(musteri2, sport2.getId(), currentDate, 2, 2400.0, 1200.0, "GUNLUK");

            // 31-40. Kiralamalar - Kurumsal 2
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            currentDate = calendar.getTime();
            rentalService.createRental(kurumsal2, sedan.getId(), currentDate, 6, 3000.0, 500.0, "GUNLUK");
            rentalService.createRental(kurumsal2, suv.getId(), currentDate, 4, 2800.0, 700.0, "GUNLUK");
            rentalService.createRental(kurumsal2, hatchback.getId(), currentDate, 2, 900.0, 450.0, "GUNLUK");
            rentalService.createRental(kurumsal2, stationWagon.getId(), currentDate, 7, 5600.0, 800.0, "GUNLUK");
            rentalService.createRental(kurumsal2, sport.getId(), currentDate, 3, 4500.0, 1500.0, "GUNLUK");
            rentalService.createRental(kurumsal2, sedan2.getId(), currentDate, 5, 3000.0, 600.0, "GUNLUK");
            rentalService.createRental(kurumsal2, suv2.getId(), currentDate, 3, 2700.0, 900.0, "GUNLUK");
            rentalService.createRental(kurumsal2, hatchback2.getId(), currentDate, 4, 1600.0, 400.0, "GUNLUK");
            rentalService.createRental(kurumsal2, stationWagon2.getId(), currentDate, 8, 6000.0, 750.0, "GUNLUK");
            rentalService.createRental(kurumsal2, sport2.getId(), currentDate, 4, 4800.0, 1200.0, "GUNLUK");

            // 41-50. Kiralamalar - Müşteri 3
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            currentDate = calendar.getTime();
            rentalService.createRental(musteri3, sedan.getId(), currentDate, 3, 1500.0, 500.0, "GUNLUK");
            rentalService.createRental(musteri3, suv.getId(), currentDate, 2, 1400.0, 700.0, "GUNLUK");
            rentalService.createRental(musteri3, hatchback.getId(), currentDate, 4, 1800.0, 450.0, "GUNLUK");
            rentalService.createRental(musteri3, stationWagon.getId(), currentDate, 3, 2400.0, 800.0, "GUNLUK");
            rentalService.createRental(musteri3, sport.getId(), currentDate, 1, 1500.0, 1500.0, "GUNLUK");
            rentalService.createRental(musteri3, sedan2.getId(), currentDate, 2, 1200.0, 600.0, "GUNLUK");
            rentalService.createRental(musteri3, suv2.getId(), currentDate, 3, 2700.0, 900.0, "GUNLUK");
            rentalService.createRental(musteri3, hatchback2.getId(), currentDate, 5, 2000.0, 400.0, "GUNLUK");
            rentalService.createRental(musteri3, stationWagon2.getId(), currentDate, 4, 3000.0, 750.0, "GUNLUK");
            rentalService.createRental(musteri3, sport2.getId(), currentDate, 2, 2400.0, 1200.0, "GUNLUK");

        } catch (SQLException e) {
            System.err.println("Varsayılan veri ekleme hatası: " + e.getMessage());
        }
    }
} 