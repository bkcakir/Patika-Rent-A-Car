package Main;

import Service.VehicleService;
import Service.UserService;
import Service.RentalService;
import Model.Vehicle;
import Model.User;
import Model.Rental;
import Model.VehicleType;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class MainAdmin {
    private static VehicleService vehicleService;
    private static UserService userService;
    private static RentalService rentalService;
    static int Count = 1;

    public static void initializeServices(VehicleService vs, UserService us, RentalService rs) {
        vehicleService = vs;
        userService = us;
        rentalService = rs;
    }

    public static void showAdminMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n=== ADMIN MENÜSÜ ===");
            System.out.println("1. Araç İşlemleri");
            System.out.println("2. Kullanıcı İşlemleri");
            System.out.println("3. Kiralama Raporları");
            System.out.println("4. Ana Menüye Dön");
            System.out.print("Seçiminiz: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Buffer temizleme

            switch (choice) {
                case 1:
                    showVehicleOperations(scanner);
                    break;
                case 2:
                    showUserOperations(scanner);
                    break;
                case 3:
                    showRentalReports(scanner);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Geçersiz seçim!");
            }
        }
    }

    private static void showVehicleOperations(Scanner scanner) {
        while (true) {
            System.out.println("\n=== ARAÇ İŞLEMLERİ ===");
            System.out.println("1. Araç Ekle");
            System.out.println("2. Araç Sil");
            System.out.println("3. Araç Güncelle");
            System.out.println("4. Tüm Araçları Listele");
            System.out.println("5. Üst Menüye Dön");
            System.out.print("Seçiminiz: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Buffer temizleme

            switch (choice) {
                case 1:
                    addVehicle(scanner);
                    break;
                case 2:
                    deleteVehicle(scanner);
                    break;
                case 3:
                    updateVehicle(scanner);
                    break;
                case 4:
                    listAllVehicles(scanner);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Geçersiz seçim!");
            }
        }
    }

    private static void showUserOperations(Scanner scanner) {
        while (true) {
            System.out.println("\n=== KULLANICI İŞLEMLERİ ===");
            System.out.println("1. Kullanıcı Listele");
            System.out.println("2. Kullanıcı Sil");
            System.out.println("3. Kullanıcı Rolü Değiştir");
            System.out.println("4. Üst Menüye Dön");
            System.out.print("Seçiminiz: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Buffer temizleme

            switch (choice) {
                case 1:
                    listUsers(scanner);
                    break;
                case 2:
                    deleteUser(scanner);
                    break;
                case 3:
                    changeUserRole(scanner);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Geçersiz seçim!");
            }
        }
    }

    private static void showRentalReports(Scanner scanner) {
        while (true) {
            System.out.println("\n=== KİRALAMA RAPORLARI ===");
            System.out.println("1. Aktif Kiralamalar");
            System.out.println("2. Tüm Kiralama Geçmişi");
            System.out.println("3. Kategori Bazlı Kiralama Raporu");
            System.out.println("4. Üst Menüye Dön");
            System.out.print("Seçiminiz: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Buffer temizleme

            switch (choice) {
                case 1:
                    listActiveRentals(scanner);
                    break;
                case 2:
                    listAllRentals(scanner);
                    break;
                case 3:
                    listRentalsByCategory(scanner);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Geçersiz seçim!");
            }
        }
    }

    private static void addVehicle(Scanner scanner) {
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

    private static void deleteVehicle(Scanner scanner) {
        try {
            System.out.print("Silinecek araç ID: ");
            int vehicleId = scanner.nextInt();
            scanner.nextLine(); // Buffer temizleme

            // Önce aracın aktif kiralama durumunu kontrol et
            List<Rental> activeRentals = rentalService.getActiveRentalsByVehicleId(vehicleId);
            if (!activeRentals.isEmpty()) {
                System.out.println("Bu araç şu anda kiralanmış durumda! Silinemez.");
                return;
            }

            vehicleService.deleteVehicle(vehicleId);
            System.out.println("Araç başarıyla silindi!");
        } catch (SQLException e) {
            System.err.println("Araç silme hatası: " + e.getMessage());
        }
    }

    private static void updateVehicle(Scanner scanner) {
        try {
            System.out.print("Güncellenecek araç ID: ");
            int id = scanner.nextInt();
            scanner.nextLine(); // Buffer temizleme

            Vehicle vehicle = vehicleService.getVehicleById(id);
            if (vehicle == null) {
                System.out.println("Araç bulunamadı!");
                return;
            }

            System.out.print("Yeni marka (Enter'a basarak atlayın): ");
            String brand = scanner.nextLine();
            if (!brand.isEmpty()) {
                vehicle.setBrand(brand);
            }

            System.out.print("Yeni model (Enter'a basarak atlayın): ");
            String model = scanner.nextLine();
            if (!model.isEmpty()) {
                vehicle.setModel(model);
            }

            System.out.print("Yeni yıl (0'a basarak atlayın): ");
            int year = scanner.nextInt();
            if (year != 0) {
                vehicle.setYear(year);
            }
            scanner.nextLine(); // Buffer temizleme

            System.out.println("Yeni araç tipi (Enter'a basarak atlayın):");
            System.out.println("1. SEDAN");
            System.out.println("2. SUV");
            System.out.println("3. HATCHBACK");
            System.out.println("4. STATION WAGON");
            System.out.println("5. SPOR");
            System.out.print("Seçiminiz (1-5): ");
            String typeChoice = scanner.nextLine();
            if (!typeChoice.isEmpty()) {
                VehicleType vehicleType;
                switch (Integer.parseInt(typeChoice)) {
                    case 1:
                        vehicle.setVehicleType(VehicleType.SEDAN);
                        break;
                    case 2:
                        vehicle.setVehicleType(VehicleType.SUV);
                        break;
                    case 3:
                        vehicle.setVehicleType(VehicleType.HATCHBACK);
                        break;
                    case 4:
                        vehicle.setVehicleType(VehicleType.STATION_WAGON);
                        break;
                    case 5:
                        vehicle.setVehicleType(VehicleType.SPOR);
                        break;
                    default:
                        System.out.println("Geçersiz seçim! Değişiklik yapılmadı.");
                        return;
                }
            }

            System.out.print("Yeni günlük fiyat (0'a basarak atlayın): ");
            double dailyPrice = scanner.nextDouble();
            if (dailyPrice != 0) {
                vehicle.setDailyPrice(dailyPrice);
            }

            System.out.print("Yeni araç bedeli (0'a basarak atlayın): ");
            double price = scanner.nextDouble();
            scanner.nextLine(); // Buffer temizleme
            if (price != 0) {
                vehicle.setPrice(price);
            }
            
            System.out.print("Yeni plaka (Enter'a basarak atlayın): ");
            String plate = scanner.nextLine();
            if (!plate.isEmpty()) {
                vehicle.setPlate(plate);
            }

            vehicleService.updateVehicle(vehicle);
            System.out.println("Araç başarıyla güncellendi!");
        } catch (SQLException e) {
            System.err.println("Araç güncelleme hatası: " + e.getMessage());
        }
    }

    private static void listAllVehicles(Scanner scanner) {
        Count = 1;
        try {
            List<Vehicle> vehicles = vehicleService.getAllVehicles();
            if (vehicles.isEmpty()) {
                System.out.println("\nHenüz araç bulunmuyor!");
                return;
            }
            
            System.out.println("\n=== TÜM ARAÇLAR ===");
            System.out.println("ID  | Araç Kodu  | Marka      | Model            | Yıl  | Kategori      | Günlük    | Fiyat        | Durum    | Plaka");
            System.out.println("----|------------|------------|------------------|------|---------------|-----------|--------------|----------|--------");
            
            for (Vehicle vehicle : vehicles) {
                String model = vehicle.getModel();
                if (model.length() > 15) {
                    model = model.substring(0, 12) + "...";
                }
                
                String plate = formatPlate(vehicle.getPlate().toString().toUpperCase());
                
                System.out.println(String.format("%-3d | %-10s | %-10s | %-15s | %-4d | %-12s | %-10s | %-12s | %-8s | %-8s",
                        Count,
                        vehicle.getId(),
                        vehicle.getBrand(),
                        model,
                        vehicle.getYear(),
                        vehicle.getVehicleType().getDisplayName(),
                        String.format("%,.2f TL", vehicle.getDailyPrice()),
                        String.format("%,.2f TL", vehicle.getPrice()),
                        vehicle.isAvailable() ? "Müsait" : "Kirada",
                        plate));
                Count++;
            }
            System.out.println("\nAraç detayı görüntülemek için araç ID'sini girin (0: Çıkış): ");
            System.out.print("Seçiminiz: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Buffer temizleme

            if (choice > 0 && choice <= vehicles.size()) {
                showVehicleDetails(vehicles.get(choice - 1));
            }
            System.out.println();
            
        } catch (SQLException e) {
            System.err.println("Araç listeleme hatası: " + e.getMessage());
        }
    }

    private static void showVehicleDetails(Vehicle vehicle) {
        while (true) {
            System.out.println("\n=== ARAÇ DETAYLARI ===");
            System.out.println("Araç Kodu: " + vehicle.getId());
            System.out.println("Marka: " + vehicle.getBrand());
            System.out.println("Model: " + vehicle.getModel());
            System.out.println("Yıl: " + vehicle.getYear());
            System.out.println("Kategori: " + vehicle.getVehicleType().getDisplayName());
            System.out.println("Günlük Fiyat: " + String.format("%,.2f TL", vehicle.getDailyPrice()));
            System.out.println("Araç Bedeli: " + String.format("%,.2f TL", vehicle.getPrice()));
            System.out.println("Plaka: " + formatPlate(vehicle.getPlate()).toUpperCase());
            System.out.println("Durum: " + (vehicle.isAvailable() ? "Müsait" : "Kirada"));
            System.out.println("Kayıt Tarihi: " + vehicle.getCreatedAt());
            System.out.println("Son Güncelleme: " + vehicle.getUpdatedAt());
            System.out.println("\nGeri dönmek için 0'a basın: ");
            System.out.print("Seçiminiz: ");
            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            if (choice == 0) {
                return;
            }
        }
    }

    private static String formatPlate(String plate) {
        if (plate == null || plate.length() < 7) {
            return plate;
        }
        return plate.substring(0, 2) + " " + plate.substring(2, 5) + " " + plate.substring(5);
    }

    private static void listUsers(Scanner scanner) {
        Count = 1;
        try {
            List<User> users = userService.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("\nHenüz kullanıcı bulunmuyor!");
                return;
            }
            
            System.out.println("\n=== TÜM KULLANICILAR ===");
            System.out.println("ID  | Kullanıcı Kodu | Ad Soyad                | Email                          | Yaş  | Rol        | Kurumsal   | Şifre");
            System.out.println("----|----------------|-------------------------|--------------------------------|------|------------|------------|--------");
            
            for (User user : users) {
                String fullName = user.getName() + " " + user.getLastName();
                System.out.println(String.format("%-3d | %-14s | %-25s | %-30s | %-4d | %-10s | %-10s | %-8s",
                        Count,
                        user.getId(),
                        fullName,
                        user.getEmail(),
                        user.getAge(),
                        user.getRole(),
                        user.isCorporate() ? "Evet" : "Hayır",
                        "********"));
                Count++;
            }
            System.out.println("\nKullanıcı detayı görüntülemek için kullanıcı ID'sini girin (0: Çıkış): ");
            System.out.print("Seçiminiz: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Buffer temizleme

            if (choice > 0 && choice <= users.size()) {
                showUserDetails(users.get(choice - 1));
            }
        } catch (SQLException e) {
            System.err.println("Kullanıcı listeleme hatası: " + e.getMessage());
        }
    }

    private static void showUserDetails(User user) {
        while (true) {
            System.out.println("\n=== KULLANICI DETAYLARI ===");
            System.out.println("Kullanıcı Kodu: " + user.getId());
            System.out.println("Ad Soyad: " + user.getName() + " " + user.getLastName());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Yaş: " + user.getAge());
            System.out.println("Rol: " + user.getRole());
            System.out.println("Kurumsal: " + (user.isCorporate() ? "Evet" : "Hayır"));
            System.out.println("Kayıt Tarihi: " + user.getCreatedAt());
            System.out.println("Son Güncelleme: " + user.getUpdatedAt());
            System.out.println("\nGeri dönmek için 0'a basın: ");
            System.out.print("Seçiminiz: ");
            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            if (choice == 0) {
                return;
            }
        }
    }

    private static void deleteUser(Scanner scanner) {
        try {
            System.out.print("Silinecek kullanıcı ID: ");
            int userId = scanner.nextInt();
            scanner.nextLine(); // Buffer temizleme

            // Önce kullanıcının tüm kiralama kayıtlarını kontrol et
            List<Rental> userRentals = rentalService.getRentalsByUserId(userId);
            if (!userRentals.isEmpty()) {
                System.out.println("\nBu kullanıcının kiralama kayıtları bulunmaktadır!");
                System.out.println("Toplam kiralama sayısı: " + userRentals.size());
                System.out.println("Aktif kiralama sayısı: " + userRentals.stream().filter(Rental::isActive).count());
                System.out.println("\nKullanıcı silinemez çünkü kiralama kayıtları vardır.");
                System.out.println("Kullanıcıyı silmek için önce tüm kiralama kayıtlarının silinmesi gerekir.");
                return;
            }

            // Kullanıcıyı sil
            userService.deleteUser(userId);
            System.out.println("Kullanıcı başarıyla silindi!");
        } catch (SQLException e) {
            System.err.println("Kullanıcı silme hatası: " + e.getMessage());
        }
    }

    private static void changeUserRole(Scanner scanner) {
        try {
            System.out.print("Kullanıcı ID: ");
            int userId = scanner.nextInt();
            scanner.nextLine(); // Buffer temizleme

            System.out.println("Yeni rol seçin:");
            System.out.println("1. ADMIN");
            System.out.println("2. MUSTERI");
            System.out.println("3. KURUMSAL");
            System.out.print("Seçiminiz (1-3): ");
            int roleChoice = scanner.nextInt();
            scanner.nextLine(); // Buffer temizleme

            String newRole;
            switch (roleChoice) {
                case 1:
                    newRole = "ADMIN";
                    break;
                case 2:
                    newRole = "MUSTERI";
                    break;
                case 3:
                    newRole = "KURUMSAL";
                    break;
                default:
                    System.out.println("Geçersiz seçim!");
                    return;
            }

            userService.updateUserRole(userId, newRole);
            System.out.println("Kullanıcı rolü başarıyla güncellendi!");
        } catch (SQLException e) {
            System.err.println("Rol güncelleme hatası: " + e.getMessage());
        }
    }

    private static String formatEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "Bilinmiyor";
        }
        return email;
    }

    private static void listActiveRentals(Scanner scanner) {
        try {
            List<Rental> activeRentals = rentalService.getActiveRentals();
            if (activeRentals.isEmpty()) {
                System.out.println("\nAktif kiralama bulunmamaktadır.");
                return;
            }

            System.out.println("\n=== AKTİF KİRALAMALAR ===");
            System.out.println("ID  | Kullanıcı ID | Kullanıcı                | Plaka        | Başlangıç      | Bitiş         | Tutar      | Durum      | Kiralama Tipi");
            System.out.println("----|--------------|-------------------------|--------------|----------------|---------------|------------|------------|--------------");
            
            for (Rental rental : activeRentals) {
                String status = rental.isActive() ? "Kirada" : "Tamamlandı";
                String plate = formatPlate(rental.getVehicle().getPlate().toString().toUpperCase());
                String fullName = rental.getUser().getName();
                if (rental.getUser().getLastName() != null && !rental.getUser().getLastName().isEmpty()) {
                    fullName += " " + rental.getUser().getLastName();
                }

                // Tarihleri formatla
                String startDate = rental.getStartDate().toString().substring(0, 10);
                String endDate = rental.getEndDate().toString().substring(0, 10);

                System.out.println(String.format("%-3d | %-12d | %-25s | %-12s | %-14s | %-13s | %-10s | %-10s | %-12s",
                        rental.getId(),
                        rental.getUserId(),
                        fullName,
                        plate,
                        startDate,
                        endDate,
                        String.format("%,.2f TL", rental.getTotalPrice()),
                        status,
                        rental.getRentalType()));
            }
            System.out.println();
        } catch (SQLException e) {
            System.err.println("Aktif kiralama listeleme hatası: " + e.getMessage());
        }
    }

    private static void listAllRentals(Scanner scanner) {
        try {
            List<Rental> rentals = rentalService.getAllRentals();
            if (rentals.isEmpty()) {
                System.out.println("\nHenüz kiralama kaydı bulunmuyor!");
                return;
            }

            System.out.println("\n=== TÜM KİRALAMA GEÇMİŞİ ===");
            System.out.println("ID  | Kullanıcı ID | Kullanıcı                | Plaka        | Başlangıç      | Bitiş         | Tutar      | Durum      | Kiralama Tipi");
            System.out.println("----|--------------|-------------------------|--------------|----------------|---------------|------------|------------|--------------");

            for (Rental rental : rentals) {
                String status = rental.isActive() ? "Kirada" : "Tamamlandı";
                String plate = formatPlate(rental.getVehicle().getPlate().toString().toUpperCase());
                String fullName = rental.getUser().getName();
                if (rental.getUser().getLastName() != null && !rental.getUser().getLastName().isEmpty()) {
                    fullName += " " + rental.getUser().getLastName();
                }

                // Tarihleri formatla
                String startDate = rental.getStartDate().toString().substring(0, 10);
                String endDate = rental.getEndDate().toString().substring(0, 10);

                System.out.println(String.format("%-3d | %-12d | %-25s | %-12s | %-14s | %-13s | %-10s | %-10s | %-12s",
                        rental.getId(),
                        rental.getUserId(),
                        fullName,
                        plate,
                        startDate,
                        endDate,
                        String.format("%,.2f TL", rental.getTotalPrice()),
                        status,
                        rental.getRentalType()));
            }
            System.out.println();
        } catch (SQLException e) {
            System.err.println("Kiralama listeleme hatası: " + e.getMessage());
        }
    }

    private static void listRentalsByCategory(Scanner scanner) {
        try {
            System.out.println("\nAraç tipi seçin:");
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

            List<Rental> rentals = rentalService.getRentalsByVehicleType(vehicleType);
            if (rentals.isEmpty()) {
                System.out.println("\n" + vehicleType.getDisplayName() + " tipindeki araçlar için kiralama bulunmuyor!");
                return;
            }
            
            System.out.println("\n=== " + vehicleType.getDisplayName() + " TİPİNDEKİ KİRALAMALAR ===");
            System.out.println("ID  | Kullanıcı ID | Kullanıcı                | Plaka        | Başlangıç      | Bitiş         | Tutar      | Durum      | Kiralama Tipi");
            System.out.println("----|--------------|-------------------------|--------------|----------------|---------------|------------|------------|--------------");
            
            for (Rental rental : rentals) {
                String status = rental.isActive() ? "Kirada" : "Tamamlandı";
                String plate = formatPlate(rental.getVehicle().getPlate().toString().toUpperCase());
                String fullName = rental.getUser().getName();
                if (rental.getUser().getLastName() != null && !rental.getUser().getLastName().isEmpty()) {
                    fullName += " " + rental.getUser().getLastName();
                }

                // Tarihleri formatla
                String startDate = rental.getStartDate().toString().substring(0, 10);
                String endDate = rental.getEndDate().toString().substring(0, 10);

                System.out.println(String.format("%-3d | %-12d | %-25s | %-12s | %-14s | %-13s | %-10s | %-10s | %-12s",
                        rental.getId(),
                        rental.getUserId(),
                        fullName,
                        plate,
                        startDate,
                        endDate,
                        String.format("%,.2f TL", rental.getTotalPrice()),
                        status,
                        rental.getRentalType()));
            }
            System.out.println();
        } catch (SQLException e) {
            System.err.println("Araç tipi bazlı kiralama listeleme hatası: " + e.getMessage());
        }
    }

    private static void showRentalDetails(Scanner scanner) {
        try {
            while (true) {
                System.out.print("\nKiralama ID'sini girin (0: Çıkış): ");
                int rentalId = scanner.nextInt();
                scanner.nextLine(); // Buffer temizleme

                if (rentalId == 0) {
                    return;
                }

                Rental rental = rentalService.getRentalById(rentalId);
                if (rental == null) {
                    System.out.println("Kiralama bulunamadı!");
                    continue;
                }

                String fullName = rental.getUser().getName();
                if (rental.getUser().getLastName() != null && !rental.getUser().getLastName().isEmpty()) {
                    fullName += " " + rental.getUser().getLastName();
                }

                System.out.println("\n=== KİRALAMA DETAYLARI ===");
                System.out.println("Kiralama ID: " + rental.getId());
                System.out.println("Kullanıcı ID: " + rental.getUserId());
                System.out.println("Kullanıcı: " + fullName);
                System.out.println("Email: " + rental.getUser().getEmail());
                System.out.println("Araç: " + rental.getVehicle().getBrand() + " " + rental.getVehicle().getModel());
                System.out.println("Plaka: " + formatPlate(rental.getVehicle().getPlate().toUpperCase()));
                System.out.println("Başlangıç Tarihi: " + rental.getStartDate());
                System.out.println("Bitiş Tarihi: " + rental.getEndDate());
                System.out.println("Kiralama Tipi: " + rental.getRentalType());
                System.out.println("Toplam Tutar: " + String.format("%,.2f TL", rental.getTotalPrice()));
                System.out.println("Durum: " + (rental.isActive() ? "Kirada" : "Tamamlandı"));
                System.out.println("\nGeri dönmek için 0'a basın: ");
                System.out.print("Seçiminiz: ");
                int choice = scanner.nextInt();
                if (choice == 0) {
                    return;
                }
            }
        } catch (SQLException e) {
            System.err.println("Kiralama detay görüntüleme hatası: " + e.getMessage());
        }
    }
} 