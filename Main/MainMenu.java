package Main;

import java.util.Scanner;

public class MainMenu {
    public static void show() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== ARAÇ KİRALAMA SİSTEMİ ===");
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
                    // Varsayılan veri yükleme işlemi buraya eklenecek
                    break;
                case 4:
                    System.out.println("Program sonlandırılıyor...");
                    return;
                default:
                    System.out.println("Geçersiz seçim!");
            }
        }
    }
} 