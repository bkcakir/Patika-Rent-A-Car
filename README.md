# Patika Rent A Car

Bu proje, Java 21 ve PostgreSQL kullanılarak geliştirilmiş bir araç kiralama uygulamasıdır.

## Proje Özellikleri

### Kullanıcı Yönetimi
- Müşteriler ve sistem kullanıcıları email ve şifre ile giriş yapabilir
- Şifreler SHA-256 algoritması ile güvenli bir şekilde şifrelenir
- Sadece giriş yapmış kullanıcılar araç kiralayabilir

### Araç Yönetimi
- Araçlar ADMIN kullanıcısı tarafından sisteme eklenir
- Desteklenen araç tipleri:
  - SEDAN
  - SUV
  - HATCHBACK
  - STATION WAGON
  - SPOR
- Araçlar kategorilere göre filtrelenebilir
- Sayfalı liste görünümü

### Kiralama Özellikleri
- Kiralama süreleri:
  - Saatlik
  - Günlük
  - Haftalık
  - Aylık
- Kurumsal kullanıcılar minimum 1 aylık kiralama yapabilir
- 2 Milyon TL üzeri araçlar için:
  - 30 yaş üstü kullanıcılar kiralayabilir
  - Araç bedelinin %10'u kadar depozito gerekir
- Her araç sınıfı için farklı kiralama ücretleri

### Teknik Özellikler
- Katmanlı mimari (DAO, Service, Model, Main)
- Exception handling
- Transaction yönetimi
- İlişkisel veritabanı (PostgreSQL)
- Terminal tabanlı menülü sistem

## Teknoloji Yığını
- Java 21
- PostgreSQL

## Veritabanı Yapılandırması

### PostgreSQL Bağlantı Bilgileri
```properties
URL: jdbc:postgresql://localhost:3439/rent_a_car
Kullanıcı Adı: Admin
Şifre: Admin123*!!
```

> **ÖNEMLİ NOT:** Veritabanı bağlantı bilgilerini kendi PostgreSQL kurulumunuza göre değiştirmeniz gerekmektedir. Bu bilgiler sadece örnek olarak verilmiştir. Güvenlik için kendi kullanıcı adı ve şifrenizi kullanın.

### Veritabanı Kurulumu
1. PostgreSQL'i yükleyin
2. 3439 portunda çalışan bir PostgreSQL sunucusu oluşturun
3. `rent_a_car` adında bir veritabanı oluşturun
4. `Db/create_tables.sql` dosyasını çalıştırarak tabloları oluşturun

### Veritabanı Yedekleme ve Geri Yükleme
- Veritabanı yedek dosyası: `Db/PatikaRentAcarDb`
- Yedek dosyasını geri yüklemek için:
  1. DBeaver'da veritabanınıza sağ tıklayın
  2. "Restore" seçeneğini seçin
  3. `Db/PatikaRentAcarDb` dosyasını seçin
  4. "Start" butonuna tıklayarak geri yüklemeyi başlatın

## Proje Yapısı
```
├── Main/           # Ana uygulama sınıfları
├── Model/          # Veri modelleri
├── Service/        # İş mantığı katmanı
├── Daos/           # Veri erişim katmanı
├── Db/             # Veritabanı bağlantı ve yapılandırma
├── Helper/         # Yardımcı sınıflar
└── resources/      # Kaynak dosyaları
```

## Kurulum

1. PostgreSQL veritabanını kurun ve çalıştırın
2. `Db/create_tables.sql` dosyasını çalıştırarak veritabanı tablolarını oluşturun
3. Projeyi derleyin ve çalıştırın

## Kullanım

1. Uygulamayı başlatın
2. Ana menüde şu seçenekler sunulacaktır:
   - Giriş Yap: Mevcut hesabınızla giriş yapın
   - Kayıt Ol: Yeni bir hesap oluşturun
   - Varsayılan Verileri Yükle: Örnek kullanıcılar, araçlar ve kiralama verilerini yükleyin
   - Çıkış: Uygulamadan çıkın

> **İPUCU:** İlk kez kullanıyorsanız, "Varsayılan Verileri Yükle" seçeneğini kullanarak örnek verileri yükleyebilirsiniz. Bu seçenek:
> - Örnek admin kullanıcıları
> - Örnek müşteri hesapları
> - Örnek kurumsal hesaplar
> - Örnek araçlar
> - Örnek kiralama kayıtları
> oluşturacaktır.
>
> **ÖNEMLİ:** Varsayılan kullanıcıların şifreleri veritabanında hash'lenmiş olarak saklanmaktadır. Bu nedenle giriş yaparken kullanacağınız şifreleri `User_list.txt` dosyasından kontrol edebilirsiniz. Bu dosya, tüm varsayılan kullanıcıların email ve şifrelerini içermektedir.

3. Giriş yaptıktan sonra menüden istediğiniz işlemi seçin:
   - Araç arama
   - Kiralama
   - Kiralama geçmişi görüntüleme
   - vb.

## Güvenlik

- Tüm şifreler SHA-256 algoritması ile şifrelenir
- Kullanıcı yetkilendirmesi rol tabanlıdır
- Hassas veriler güvenli bir şekilde saklanır
