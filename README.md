# Patika Rent A Car

Bu proje, bir araç kiralama sisteminin konsol tabanlı uygulamasıdır.

## Özellikler

- Kullanıcı yönetimi (kayıt, giriş, profil yönetimi)
- Araç yönetimi (ekleme, silme, güncelleme, listeleme)
- Kiralama işlemleri (araç kiralamak, iade etmek)
- Admin paneli
- Kullanıcı paneli

## Gereksinimler

- Java 17 veya üzeri
- Maven
- PostgreSQL 12 veya üzeri

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

## Proje Yapısı

```
src/main/java/
├── Main/           # Ana uygulama sınıfları
├── Model/          # Veri modelleri
├── Service/        # İş mantığı katmanı
├── Daos/           # Veritabanı erişim katmanı
├── Db/             # Veritabanı bağlantı ve yardımcı sınıflar
└── Helper/         # Yardımcı sınıflar
```

## Lisans

Bu proje MIT lisansı altında lisanslanmıştır. Detaylar için [LICENSE](LICENSE) dosyasına bakın.
