package Main;

import Service.RentalService;
import Service.VehicleService;
import Model.Rental;
import Model.User;
import java.sql.SQLException;
import java.sql.Date;
import java.util.List;
import java.util.Scanner;
import Model.Vehicle;
import java.util.Calendar;
import java.util.stream.Collectors;

public class MainRental {
    static int Count = 1;
    public static void rentVehicle(Scanner scanner, User currentUser, RentalService rentalService, VehicleService vehicleService) {

        try {
            System.out.println("\n=== Araç Kirala ===");
            
            // Önce müsait araçları listele
            List<Vehicle> availableVehicles = vehicleService.getAvailableVehicles();
            if (availableVehicles.isEmpty()) {
                System.out.println("Müsait araç bulunmamaktadır!");
                return;
            }
            
            System.out.println("\nMüsait Araçlar:");
            System.out.println("ID  | Marka      | Model            | Yıl  | Kategori      | Günlük    | Fiyat        | Plaka");
            System.out.println("----|------------|------------------|------|---------------|-----------|--------------|--------");
            for (Vehicle vehicle : availableVehicles) {
                System.out.println(String.format("%-3d | %-10s | %-15s | %-4d | %-12s | %-10.2f | %-12.2f | %-8s",
                    vehicle.getId(),
                    vehicle.getBrand(),
                    vehicle.getModel(),
                    vehicle.getYear(),
                    vehicle.getVehicleType().getDisplayName(),
                    vehicle.getDailyPrice(),
                    vehicle.getPrice(),
                    vehicle.getPlate()));
            }
            System.out.println();

            // Plaka kontrolü
            String plate;
            Vehicle selectedVehicle = null;
            while (true) {
                try {
                    System.out.print("Kiralanacak aracın plakasını girin: ");
                    String inputPlate = scanner.nextLine().toUpperCase();
                    
                    // Plakayı ara
                    selectedVehicle = availableVehicles.stream()
                        .filter(v -> v.getPlate().equals(inputPlate))
                        .findFirst()
                        .orElse(null);
                    
                    if (selectedVehicle == null) {
                        System.out.println("Hata: Bu plakaya sahip müsait araç bulunamadı!");
                        continue;
                    }
                    plate = inputPlate;
                    break;
                } catch (Exception e) {
                    System.out.println("Hata: Lütfen geçerli bir plaka giriniz!");
                }
            }

            if (selectedVehicle != null) {
                // Seçilen aracın müsait olup olmadığını kontrol et
                if (!selectedVehicle.isAvailable()) {
                    System.out.println("Seçilen araç müsait değil!");
                    return;
                }

                // Tarih kontrolü
                Date startDate;
                while (true) {
                    try {
                        System.out.print("Başlangıç tarihi (YYYY-MM-DD): ");
                        String startDateStr = scanner.nextLine();
                        startDate = Date.valueOf(startDateStr);
                        
                        // Geçmiş tarih kontrolü
                        Date currentDate = new Date(System.currentTimeMillis());
                        if (startDate.before(currentDate)) {
                            System.out.println("Hata: Başlangıç tarihi bugünden önce olamaz!");
                            continue;
                        }
                        break;
                    } catch (IllegalArgumentException e) {
                        System.out.println("Hata: Lütfen tarihi YYYY-MM-DD formatında giriniz! (Örnek: 2024-05-20)");
                    }
                }

                // Kiralama tipi kontrolü
                String rentalType;
                while (true) {
                    System.out.println("\nKiralama Tipi Seçin:");
                    System.out.println("1. Saatlik");
                    System.out.println("2. Günlük");
                    System.out.println("3. Haftalık");
                    System.out.println("4. Aylık");
                    System.out.print("Seçiminiz (1-4): ");
                    
                    try {
                        int rentalTypeChoice = scanner.nextInt();
                        scanner.nextLine(); // Buffer temizleme
                        
                        switch (rentalTypeChoice) {
                            case 1:
                                rentalType = "SAATLIK";
                                break;
                            case 2:
                                rentalType = "GUNLUK";
                                break;
                            case 3:
                                rentalType = "HAFTALIK";
                                break;
                            case 4:
                                rentalType = "AYLIK";
                                break;
                            default:
                                System.out.println("Hata: Lütfen 1-4 arasında bir sayı giriniz!");
                                continue;
                        }
                        break;
                    } catch (Exception e) {
                        System.out.println("Hata: Lütfen geçerli bir sayı giriniz!");
                        scanner.nextLine(); // Hatalı girişi temizle
                    }
                }

                // Kiralama süresini al
                int duration = 0;
                while (true) {
                    try {
                        switch (rentalType) {
                            case "SAATLIK":
                                System.out.print("Kaç saat kiralamak istiyorsunuz? (1-24): ");
                                duration = scanner.nextInt();
                                if (duration < 1 || duration > 24) {
                                    System.out.println("Hata: Süre 1-24 saat arasında olmalıdır!");
                                    continue;
                                }
                                break;
                            case "GUNLUK":
                                System.out.print("Kaç gün kiralamak istiyorsunuz? (1-7): ");
                                duration = scanner.nextInt();
                                if (duration < 1 || duration > 7) {
                                    System.out.println("Hata: Süre 1-7 gün arasında olmalıdır!");
                                    continue;
                                }
                                break;
                            case "HAFTALIK":
                                System.out.print("Kaç hafta kiralamak istiyorsunuz? (1-4): ");
                                duration = scanner.nextInt();
                                if (duration < 1 || duration > 4) {
                                    System.out.println("Hata: Süre 1-4 hafta arasında olmalıdır!");
                                    continue;
                                }
                                break;
                            case "AYLIK":
                                System.out.print("Kaç ay kiralamak istiyorsunuz? (1-12): ");
                                duration = scanner.nextInt();
                                if (duration < 1 || duration > 12) {
                                    System.out.println("Hata: Süre 1-12 ay arasında olmalıdır!");
                                    continue;
                                }
                                break;
                        }
                        break;
                    } catch (Exception e) {
                        System.out.println("Hata: Lütfen geçerli bir sayı giriniz!");
                        scanner.nextLine(); // Hatalı girişi temizle
                    }
                }
                scanner.nextLine(); // Buffer temizleme

                // Fiyat hesaplama
                double basePrice = selectedVehicle.getDailyPrice() * getRentalDuration(rentalType, duration);
                double totalPrice;
                switch (rentalType) {
                    case "SAATLIK":
                        totalPrice = basePrice * 0.1;
                        break;
                    case "HAFTALIK":
                        totalPrice = basePrice * 0.8;
                        break;
                    case "AYLIK":
                        totalPrice = basePrice * 0.7;
                        break;
                    default:
                        totalPrice = basePrice;
                }

                // Depozito hesaplama (2 milyon TL üzeri araçlar için %10)
                double depositAmount = selectedVehicle.getPrice() > 2000000 ? selectedVehicle.getPrice() * 0.1 : 0;

                // Bitiş tarihini hesapla
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                switch (rentalType) {
                    case "SAATLIK":
                        calendar.add(Calendar.HOUR_OF_DAY, duration);
                        break;
                    case "GUNLUK":
                        calendar.add(Calendar.DAY_OF_MONTH, duration);
                        break;
                    case "HAFTALIK":
                        calendar.add(Calendar.WEEK_OF_YEAR, duration);
                        break;
                    case "AYLIK":
                        calendar.add(Calendar.MONTH, duration);
                        break;
                }
                Date endDate = new Date(calendar.getTimeInMillis());

                // Kiralama detaylarını göster ve onay iste
                System.out.println("\n=== KİRALAMA DETAYLARI ===");
                System.out.println("Araç: " + selectedVehicle.getBrand() + " " + selectedVehicle.getModel());
                System.out.println("Başlangıç Tarihi: " + startDate);
                System.out.println("Bitiş Tarihi: " + endDate);
                System.out.println("Kiralama Tipi: " + rentalType);
                System.out.println("Toplam Tutar: " + totalPrice + " TL");
                if (depositAmount > 0) {
                    System.out.println("Depozito Tutarı: " + depositAmount + " TL");
                }
                
                // Onay kontrolü
                String onay;
                while (true) {
                    System.out.print("\nKiralama işlemini onaylıyor musunuz? (E/H): ");
                    onay = scanner.next().toUpperCase();
                    if (onay.equals("E") || onay.equals("H")) {
                        break;
                    }
                    System.out.println("Hata: Lütfen 'E' veya 'H' giriniz!");
                }
                
                if (onay.equals("E")) {
                    // Kiralama işlemini gerçekleştir
                    rentalService.createRental(currentUser, selectedVehicle.getId(), startDate, duration, totalPrice, depositAmount, rentalType);
                    System.out.println("\nAraç başarıyla kiralandı!");
                } else {
                    System.out.println("\nKiralama işlemi iptal edildi.");
                }
            } else {
                System.out.println("Hata: Araç seçilemedi!");
                return;
            }
        } catch (SQLException e) {
            System.err.println("Kiralama hatası: " + e.getMessage());
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

    public static void listUserRentals(Scanner scanner, RentalService rentalService, User currentUser) {
        Count = 1;
        try {
            List<Rental> rentals = rentalService.getRentalsByUserId(currentUser.getId());
            if (rentals.isEmpty()) {
                System.out.println("\nHenüz kiralama geçmişiniz bulunmuyor.");
                return;
            }
            
            System.out.println("\n=== KİRALAMA GEÇMİŞİNİZ ===");
            System.out.println("ID  | Kiralama Kodu | Araç Bilgisi          | Plaka        | Başlangıç Tarihi | Bitiş Tarihi   | Toplam Tutar | Kiralama Durumu | Kiralama Tipi");
            System.out.println("----|---------------|----------------------|--------------|------------------|----------------|--------------|----------------|--------------");
            
            for (Rental rental : rentals) {
                String vehicleInfo = rental.getVehicle().getBrand() + " " + rental.getVehicle().getModel();
                if (vehicleInfo.length() > 20) {
                    vehicleInfo = vehicleInfo.substring(0, 17) + "...";
                }
                
                String plate = rental.getVehicle().getPlate();
                
                // Tarihleri formatla
                String startDate = rental.getStartDate().toString().substring(0, 10);
                String endDate = rental.getEndDate().toString().substring(0, 10);
                
                System.out.println(String.format("%-3d | %-13d | %-20s | %-12s | %-16s | %-14s | %-12.2f | %-14s | %-12s",
                        Count,
                        rental.getId(),
                        vehicleInfo,
                        plate,
                        startDate,
                        endDate,
                        rental.getTotalPrice(),
                        rental.getStatus(),
                        rental.getRentalType()));
                Count++;
            }
            System.out.println();

            // Kiralama detaylarını görüntüleme seçeneği
            while (true) {
                try {
                    System.out.print("\nDetaylı bilgi görmek istediğiniz kiralama ID'sini girin (0: Çıkış): ");
                    int rentalId = scanner.nextInt();
                    scanner.nextLine(); // Buffer temizleme

                    if (rentalId == 0) {
                        break;
                    }

                    Rental selectedRental = rentals.stream()
                        .filter(r -> r.getId() == rentalId)
                        .findFirst()
                        .orElse(null);

                    if (selectedRental == null) {
                        System.out.println("Hata: Belirtilen ID'ye sahip kiralama bulunamadı!");
                        continue;
                    }

                    // Kiralama detaylarını göster
                    System.out.println("\n=== KİRALAMA DETAYLARI ===");
                    System.out.println("Kiralama ID: " + selectedRental.getId());
                    System.out.println("Araç: " + selectedRental.getVehicle().getBrand() + " " + selectedRental.getVehicle().getModel());
                    System.out.println("Plaka: " + selectedRental.getVehicle().getPlate());
                    System.out.println("Başlangıç Tarihi: " + selectedRental.getStartDate());
                    System.out.println("Bitiş Tarihi: " + selectedRental.getEndDate());
                    System.out.println("Kiralama Tipi: " + selectedRental.getRentalType());
                    System.out.println("Toplam Tutar: " + selectedRental.getTotalPrice() + " TL");
                    if (selectedRental.getDepositAmount() > 0) {
                        System.out.println("Depozito Tutarı: " + selectedRental.getDepositAmount() + " TL");
                    }
                    System.out.println("Durum: " + selectedRental.getStatus());

                } catch (Exception e) {
                    System.out.println("Hata: Lütfen geçerli bir sayı giriniz!");
                    scanner.nextLine(); // Hatalı girişi temizle
                }
            }
        } catch (SQLException e) {
            System.err.println("Kiralama geçmişi listeleme hatası: " + e.getMessage());
        }
    }

    public static void cancelRental(Scanner scanner, RentalService rentalService, User currentUser) {
        Count = 1;
        try {
            List<Rental> activeRentals = rentalService.getRentalsByUserId(currentUser.getId()).stream()
                .filter(r -> r.getStatus().equals("AKTIF"))
                .collect(Collectors.toList());

            if (activeRentals.isEmpty()) {
                System.out.println("\nAktif kiralama kaydınız bulunmamaktadır.");
                return;
            }

            System.out.println("\n=== AKTİF KİRALAMALARINIZ ===");
            System.out.println("ID  | Araç           | Başlangıç      | Bitiş         | Tutar      | Kiralama Tipi");
            System.out.println("----|----------------|----------------|---------------|------------|--------------");
            
            for (Rental rental : activeRentals) {
                System.out.println(String.format("%-3d | %-14s | %-14s | %-13s | %-10.2f | %-12s",
                    Count,
                    rental.getVehicle().getBrand() + " " + rental.getVehicle().getModel(),
                    rental.getStartDate(),
                    rental.getEndDate(),
                    rental.getTotalPrice(),
                    rental.getRentalType()));
                Count++;
            }

            while (true) {
                try {
                    System.out.print("\nİptal etmek istediğiniz kiralama ID'sini girin (0: Çıkış): ");
                    int rentalId = scanner.nextInt();
                    scanner.nextLine(); // Buffer temizleme

                    if (rentalId == 0) {
                        break;
                    }

                    Rental selectedRental = activeRentals.stream()
                        .filter(r -> r.getId() == rentalId)
                        .findFirst()
                        .orElse(null);

                    if (selectedRental == null) {
                        System.out.println("Hata: Belirtilen ID'ye sahip aktif kiralama bulunamadı!");
                        continue;
                    }

                    // İptal onayı
                    while (true) {
                        System.out.print("\nKiralama işlemini iptal etmek istediğinizden emin misiniz? (E/H): ");
                        String onay = scanner.next().toUpperCase();
                        if (onay.equals("E") || onay.equals("H")) {
                            if (onay.equals("E")) {
                                rentalService.cancelRental(rentalId);
                                System.out.println("\nKiralama başarıyla iptal edildi.");
                            } else {
                                System.out.println("\nİptal işlemi iptal edildi.");
                            }
                            break;
                        }
                        System.out.println("Hata: Lütfen 'E' veya 'H' giriniz!");
                    }
                    break;

                } catch (Exception e) {
                    System.out.println("Hata: Lütfen geçerli bir sayı giriniz!");
                    scanner.nextLine(); // Hatalı girişi temizle
                }
            }

        } catch (SQLException e) {
            System.err.println("Kiralama iptal hatası: " + e.getMessage());
        }
    }

    public static void listActiveRentals(Scanner scanner, RentalService rentalService, User currentUser) {
        Count = 1;
        try {
            List<Rental> activeRentals = rentalService.getRentalsByUserId(currentUser.getId()).stream()
                .filter(r -> r.getEndDate().after(new Date(System.currentTimeMillis())))
                .collect(Collectors.toList());

            if (activeRentals.isEmpty()) {
                System.out.println("\nAktif kiralama kaydınız bulunmamaktadır.");
                return;
            }

            System.out.println("\n=== AKTİF KİRALAMALARINIZ ===");
            System.out.println("ID  | Kiralama Kodu | Araç Bilgisi          | Plaka        | Başlangıç Tarihi | Bitiş Tarihi   | Toplam Tutar | Kiralama Süresi | Kiralama Tipi");
            System.out.println("----|---------------|----------------------|--------------|------------------|----------------|--------------|----------------|--------------");
            
            for (Rental rental : activeRentals) {
                String vehicleInfo = rental.getVehicle().getBrand() + " " + rental.getVehicle().getModel();
                if (vehicleInfo.length() > 20) {
                    vehicleInfo = vehicleInfo.substring(0, 17) + "...";
                }
                
                String plate = rental.getVehicle().getPlate();
                
                // Tarihleri formatla
                String startDate = rental.getStartDate().toString().substring(0, 10);
                String endDate = rental.getEndDate().toString().substring(0, 10);
                
                // Kiralama süresini hesapla
                long rentalDuration = rental.getEndDate().getTime() - rental.getStartDate().getTime();
                long rentalDays = rentalDuration / (24 * 60 * 60 * 1000);
                String rentalDurationStr = rentalDays + " gün";
                
                System.out.println(String.format("%-3d | %-13d | %-20s | %-12s | %-16s | %-14s | %-12.2f | %-14s | %-12s",
                    Count,
                    rental.getId(),
                    vehicleInfo,
                    plate,
                    startDate,
                    endDate,
                    rental.getTotalPrice(),
                    rentalDurationStr,
                    rental.getRentalType()));
                Count++;
            }
            System.out.println();

            // Kiralama detaylarını görüntüleme seçeneği
            while (true) {
                try {
                    System.out.print("\nDetaylı bilgi görmek istediğiniz kiralama ID'sini girin (0: Çıkış): ");
                    int rentalId = scanner.nextInt();
                    scanner.nextLine(); // Buffer temizleme

                    if (rentalId == 0) {
                        break;
                    }

                    Rental selectedRental = activeRentals.stream()
                        .filter(r -> r.getId() == rentalId)
                        .findFirst()
                        .orElse(null);

                    if (selectedRental == null) {
                        System.out.println("Hata: Belirtilen ID'ye sahip aktif kiralama bulunamadı!");
                        continue;
                    }

                    // Kiralama detaylarını göster
                    System.out.println("\n=== KİRALAMA DETAYLARI ===");
                    System.out.println("Kiralama ID: " + selectedRental.getId());
                    System.out.println("Araç: " + selectedRental.getVehicle().getBrand() + " " + selectedRental.getVehicle().getModel());
                    System.out.println("Plaka: " + selectedRental.getVehicle().getPlate());
                    System.out.println("Başlangıç Tarihi: " + selectedRental.getStartDate().toString().substring(0, 10));
                    System.out.println("Bitiş Tarihi: " + selectedRental.getEndDate().toString().substring(0, 10));
                    System.out.println("Kiralama Tipi: " + selectedRental.getRentalType());
                    System.out.println("Toplam Tutar: " + selectedRental.getTotalPrice() + " TL");
                    if (selectedRental.getDepositAmount() > 0) {
                        System.out.println("Depozito Tutarı: " + selectedRental.getDepositAmount() + " TL");
                    }
                    
                    // Kiralama süresini hesapla ve göster
                    long rentalDuration = selectedRental.getEndDate().getTime() - selectedRental.getStartDate().getTime();
                    long rentalDays = rentalDuration / (24 * 60 * 60 * 1000);
                    System.out.println("Kiralama Süresi: " + rentalDays + " gün");
                    System.out.println("Durum: Kirada");

                    // Kalan süre hesaplama
                    long remainingTime = selectedRental.getEndDate().getTime() - System.currentTimeMillis();
                    if (remainingTime > 0) {
                        long remainingDays = remainingTime / (24 * 60 * 60 * 1000);
                        long remainingHours = (remainingTime % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
                        System.out.println("Kalan Süre: " + remainingDays + " gün " + remainingHours + " saat");
                    } else {
                        System.out.println("Kalan Süre: Süre dolmuş");
                    }

                } catch (Exception e) {
                    System.out.println("Hata: Lütfen geçerli bir sayı giriniz!");
                    scanner.nextLine(); // Hatalı girişi temizle
                }
            }

        } catch (SQLException e) {
            System.err.println("Aktif kiralama listeleme hatası: " + e.getMessage());
        }
    }

    public static void returnVehicle(Scanner scanner, RentalService rentalService, User currentUser) {
        Count = 1;
        try {
            List<Rental> activeRentals = rentalService.getRentalsByUserId(currentUser.getId()).stream()
                .filter(r -> r.getEndDate().after(new Date(System.currentTimeMillis())))
                .collect(Collectors.toList());

            if (activeRentals.isEmpty()) {
                System.out.println("\nTeslim edilecek aktif kiralama kaydınız bulunmamaktadır.");
                return;
            }

            System.out.println("\n=== TESLİM EDİLECEK ARAÇLAR ===");
            System.out.println("ID  | Kiralama Kodu | Araç Bilgisi          | Plaka        | Başlangıç Tarihi | Bitiş Tarihi   | Toplam Tutar | Kiralama Süresi | Kiralama Tipi");
            System.out.println("----|---------------|----------------------|--------------|------------------|----------------|--------------|----------------|--------------");
            
            for (Rental rental : activeRentals) {
                String vehicleInfo = rental.getVehicle().getBrand() + " " + rental.getVehicle().getModel();
                if (vehicleInfo.length() > 20) {
                    vehicleInfo = vehicleInfo.substring(0, 17) + "...";
                }
                
                String plate = rental.getVehicle().getPlate();
                
                // Tarihleri formatla
                String startDate = rental.getStartDate().toString().substring(0, 10);
                String endDate = rental.getEndDate().toString().substring(0, 10);
                
                // Kiralama süresini hesapla
                long rentalDuration = rental.getEndDate().getTime() - rental.getStartDate().getTime();
                long rentalDays = rentalDuration / (24 * 60 * 60 * 1000);
                String rentalDurationStr = rentalDays + " gün";
                
                System.out.println(String.format("%-3d | %-13d | %-20s | %-12s | %-16s | %-14s | %-12.2f | %-14s | %-12s",
                    Count,
                    rental.getId(),
                    vehicleInfo,
                    plate,
                    startDate,
                    endDate,
                    rental.getTotalPrice(),
                    rentalDurationStr,
                    rental.getRentalType()));
                Count++;
            }

            while (true) {
                System.out.print("\nTeslim etmek istediğiniz kiralama ID'sini girin (0: Çıkış): ");
                String input = scanner.nextLine().trim();
                
                if (input.equals("0")) {
                    break;
                }

                int rentalId;
                try {
                    rentalId = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Hata: Lütfen geçerli bir sayı giriniz!");
                    continue;
                }

                Rental selectedRental = activeRentals.stream()
                    .filter(r -> r.getId() == rentalId)
                    .findFirst()
                    .orElse(null);

                if (selectedRental == null) {
                    System.out.println("Hata: Belirtilen ID'ye sahip aktif kiralama bulunamadı!");
                    continue;
                }

                // Kiralama detaylarını göster
                System.out.println("\n=== KİRALAMA DETAYLARI ===");
                System.out.println("Kiralama ID: " + selectedRental.getId());
                System.out.println("Araç: " + selectedRental.getVehicle().getBrand() + " " + selectedRental.getVehicle().getModel());
                System.out.println("Plaka: " + selectedRental.getVehicle().getPlate());
                System.out.println("Başlangıç Tarihi: " + selectedRental.getStartDate().toString().substring(0, 10));
                System.out.println("Bitiş Tarihi: " + selectedRental.getEndDate().toString().substring(0, 10));
                System.out.println("Kiralama Tipi: " + selectedRental.getRentalType());
                System.out.println("Toplam Tutar: " + selectedRental.getTotalPrice() + " TL");
                if (selectedRental.getDepositAmount() > 0) {
                    System.out.println("Depozito Tutarı: " + selectedRental.getDepositAmount() + " TL");
                }
                System.out.println("Durum: Kirada");

                // Erken teslim kontrolü
                Date currentDate = new Date(System.currentTimeMillis());
                if (currentDate.before(selectedRental.getEndDate())) {
                    long diffInMillies = selectedRental.getEndDate().getTime() - currentDate.getTime();
                    long diffInDays = diffInMillies / (24 * 60 * 60 * 1000);
                    long diffInHours = (diffInMillies % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
                    System.out.println("\nErken Teslim Bilgisi:");
                    System.out.println("Kalan Süre: " + diffInDays + " gün " + diffInHours + " saat");
                    System.out.println("Not: Erken teslim durumunda kalan süre için ücret iadesi yapılacaktır.");
                }

                // Teslim onayı
                while (true) {
                    System.out.print("\nAracı teslim etmek istediğinizden emin misiniz? (E/H): ");
                    String onay = scanner.nextLine().trim().toUpperCase();
                    
                    if (onay.equals("E")) {
                        rentalService.returnVehicle(selectedRental.getId());
                        
                        // Güncel kiralama bilgilerini al
                        Rental updatedRental = rentalService.getRentalById(selectedRental.getId());
                        System.out.println("\nAraç başarıyla teslim edildi.");
                        System.out.println("Kiralama Durumu: Tamamlandı");
                        
                        // Kiralama süresini hesapla
                        long rentalDuration = updatedRental.getEndDate().getTime() - updatedRental.getStartDate().getTime();
                        long rentalDays = rentalDuration / (24 * 60 * 60 * 1000);
                        System.out.println("Kiralama Süresi: " + rentalDays + " gün");
                        
                        if (updatedRental.getRefundAmount() > 0) {
                            System.out.println("İade Tutarı: " + updatedRental.getRefundAmount() + " TL");
                            System.out.println("İade tutarı 3 iş günü içinde hesabınıza yatırılacaktır.");
                        }
                        return;
                    } else if (onay.equals("H")) {
                        System.out.println("\nTeslim işlemi iptal edildi.");
                        return;
                    } else {
                        System.out.println("Hata: Lütfen 'E' veya 'H' giriniz!");
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Araç teslim hatası: " + e.getMessage());
        }
    }

    public static void showUserRentedVehicles(Scanner scanner, RentalService rentalService, User currentUser) {
        Count = 1;
        try {
            List<Rental> userRentals = rentalService.getRentalsByUserId(currentUser.getId());
            if (userRentals.isEmpty()) {
                System.out.println("\nHenüz kiraladığınız araç bulunmamaktadır.");
                return;
            }

            System.out.println("\n=== KİRALADIĞINIZ ARAÇLAR ===");
            System.out.println("ID  | Araç Bilgisi          | Plaka        | Başlangıç      | Bitiş         | Tutar      | Durum      | Kiralama Tipi");
            System.out.println("----|----------------------|--------------|----------------|---------------|------------|------------|--------------");
            
            for (Rental rental : userRentals) {
                String vehicleInfo = rental.getVehicle().getBrand() + " " + rental.getVehicle().getModel();
                if (vehicleInfo.length() > 20) {
                    vehicleInfo = vehicleInfo.substring(0, 17) + "...";
                }
                
                String plate = formatPlate(rental.getVehicle().getPlate());
                
                System.out.println(String.format("%-3d | %-20s | %-12s | %-14s | %-13s | %-10.2f | %-10s | %-12s",
                    Count,
                    vehicleInfo,
                    plate,
                    rental.getStartDate(),
                    rental.getEndDate(),
                    rental.getTotalPrice(),
                    rental.getStatus(),
                    rental.getRentalType()));
                Count++;
            }
            System.out.println();

            // Kiralama detaylarını görüntüleme seçeneği
            while (true) {
                try {
                    System.out.print("\nDetaylı bilgi görmek istediğiniz kiralama ID'sini girin (0: Çıkış): ");
                    int rentalId = scanner.nextInt();
                    scanner.nextLine(); // Buffer temizleme

                    if (rentalId == 0) {
                        break;
                    }

                    Rental selectedRental = userRentals.stream()
                        .filter(r -> r.getId() == rentalId)
                        .findFirst()
                        .orElse(null);

                    if (selectedRental == null) {
                        System.out.println("Hata: Belirtilen ID'ye sahip kiralama bulunamadı!");
                        continue;
                    }

                    // Kiralama detaylarını göster
                    System.out.println("\n=== KİRALAMA DETAYLARI ===");
                    System.out.println("Kiralama ID: " + selectedRental.getId());
                    System.out.println("Araç: " + selectedRental.getVehicle().getBrand() + " " + selectedRental.getVehicle().getModel());
                    System.out.println("Plaka: " + selectedRental.getVehicle().getPlate());
                    System.out.println("Başlangıç Tarihi: " + selectedRental.getStartDate());
                    System.out.println("Bitiş Tarihi: " + selectedRental.getEndDate());
                    System.out.println("Kiralama Tipi: " + selectedRental.getRentalType());
                    System.out.println("Toplam Tutar: " + selectedRental.getTotalPrice() + " TL");
                    if (selectedRental.getDepositAmount() > 0) {
                        System.out.println("Depozito Tutarı: " + selectedRental.getDepositAmount() + " TL");
                    }
                    System.out.println("Durum: " + selectedRental.getStatus());

                    // Kalan süre hesaplama (eğer kiralama aktifse)
                    if (selectedRental.getStatus().equals("AKTIF")) {
                        long remainingTime = selectedRental.getEndDate().getTime() - System.currentTimeMillis();
                        if (remainingTime > 0) {
                            long remainingDays = remainingTime / (24 * 60 * 60 * 1000);
                            long remainingHours = (remainingTime % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
                            System.out.println("Kalan Süre: " + remainingDays + " gün " + remainingHours + " saat");
                        } else {
                            System.out.println("Kalan Süre: Süre dolmuş");
                        }
                    }

                } catch (Exception e) {
                    System.out.println("Hata: Lütfen geçerli bir sayı giriniz!");
                    scanner.nextLine(); // Hatalı girişi temizle
                }
            }

        } catch (SQLException e) {
            System.err.println("Kiralama listeleme hatası: " + e.getMessage());
        }
    }

    private static String formatPlate(String plate) {
        if (plate == null || plate.isEmpty()) {
            return "";
        }
        // Plakayı büyük harfe çevir ve boşlukları kaldır
        plate = plate.toUpperCase().replaceAll("\\s+", "");
        
        // Plaka formatını kontrol et (34ABC123 gibi)
        if (plate.matches("\\d{2}[A-Z]{1,3}\\d{2,4}")) {
            // İlk iki rakam, sonra harfler, sonra rakamlar
            return plate.substring(0, 2) + " " + 
                   plate.substring(2, plate.length() - 2) + " " + 
                   plate.substring(plate.length() - 2);
        }
        return plate;
    }
} 