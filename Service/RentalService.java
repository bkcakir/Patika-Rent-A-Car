package Service;

import Daos.RentalDao;
import Daos.VehicleDao;
import Model.Rental;
import Model.Vehicle;
import Model.VehicleType;
import Model.User;
import java.sql.SQLException;
import java.util.List;
import java.util.Date;
import java.util.Calendar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RentalService {
    private final RentalDao rentalDao;
    private final VehicleDao vehicleDao;

    public RentalService(RentalDao rentalDao, VehicleDao vehicleDao) {
        this.rentalDao = rentalDao;
        this.vehicleDao = vehicleDao;
    }

    public Rental createRental(User user, int vehicleId, Date startDate, int duration,
                             double totalPrice, double depositAmount, String rentalType) throws SQLException {
        // Araç müsaitlik kontrolü
        Vehicle vehicle = vehicleDao.findById(vehicleId);
        if (vehicle == null) {
            throw new SQLException("Araç bulunamadı.");
        }
        if (!vehicle.isAvailable()) {
            throw new SQLException("Araç şu anda müsait değil.");
        }

        // Kurumsal kullanıcı kontrolü
        if (user.isCorporate() && !rentalType.equals("AYLIK")) {
            throw new SQLException("Kurumsal kullanıcılar sadece aylık kiralama yapabilir.");
        }

        // Yaş kontrolü (2 milyon TL üzeri araçlar için)
        if (vehicle.getPrice() > 2000000 && user.getAge() <= 30) {
            throw new SQLException("2 milyon TL üzeri araçları sadece 30 yaşından büyükler kiralayabilir.");
        }

        // Depozito kontrolü (2 milyon TL üzeri araçlar için)
        if (vehicle.getPrice() > 2000000 && depositAmount < (vehicle.getPrice() * 0.1)) {
            throw new SQLException("2 milyon TL üzeri araçlar için araç bedelinin %10'u kadar depozito gereklidir.");
        }

        // Kiralama süresi kontrolü
        switch (rentalType) {
            case "SAATLIK":
                if (duration > 24) {
                    throw new SQLException("Saatlik kiralama en fazla 24 saat olabilir.");
                }
                break;
            case "GUNLUK":
                if (duration > 7) {
                    throw new SQLException("Günlük kiralama en fazla 7 gün olabilir.");
                }
                break;
            case "HAFTALIK":
                if (duration < 1 || duration > 4) {
                    throw new SQLException("Haftalık kiralama 1-4 hafta arası olmalıdır.");
                }
                break;
            case "AYLIK":
                if (duration < 1) {
                    throw new SQLException("Aylık kiralama en az 1 ay olmalıdır.");
                }
                break;
        }

        // Kiralama oluştur
        Rental rental = new Rental(user.getId(), vehicleId, startDate, null, totalPrice, depositAmount, rentalType);
        rental = rentalDao.save(rental, duration);

        // Aracın müsaitlik durumunu güncelle
        vehicle.setAvailable(false);
        vehicleDao.update(vehicle);

        return rental;
    }

    public List<Rental> getAllRentals() throws SQLException {
        return rentalDao.findAll();
    }

    public Rental getRentalById(int id) throws SQLException {
        return rentalDao.findById(id);
    }

    public List<Rental> getRentalsByUserId(int userId) throws SQLException {
        return rentalDao.findByUserId(userId);
    }

    public List<Rental> getRentalsByVehicleId(int vehicleId) throws SQLException {
        return rentalDao.findByVehicleId(vehicleId);
    }

    public Rental updateRental(Rental rental) throws SQLException {
        Rental existingRental = rentalDao.findById(rental.getId());
        if (existingRental == null) {
            throw new SQLException("Kiralama kaydı bulunamadı.");
        }
        return rentalDao.update(rental);
    }

    public void deleteRental(int id) throws SQLException {
        Rental rental = rentalDao.findById(id);
        if (rental == null) {
            throw new SQLException("Kiralama kaydı bulunamadı.");
        }

        // Aracın müsaitlik durumunu güncelle
        Vehicle vehicle = vehicleDao.findById(rental.getVehicleId());
        if (vehicle != null) {
            vehicle.setAvailable(true);
            vehicleDao.update(vehicle);
        }

        rentalDao.delete(id);
    }

    public Rental completeRental(int rentalId) throws SQLException {
        Rental rental = rentalDao.findById(rentalId);
        if (rental == null) {
            throw new SQLException("Kiralama kaydı bulunamadı.");
        }

        // Aracın müsaitlik durumunu güncelle
        Vehicle vehicle = vehicleDao.findById(rental.getVehicleId());
        if (vehicle != null) {
            vehicle.setAvailable(true);
            vehicleDao.update(vehicle);
        }

        rental.setActive(false);
        return rentalDao.update(rental);
    }

    public List<Rental> getActiveRentalsByVehicleId(int vehicleId) throws SQLException {
        return rentalDao.findActiveByVehicleId(vehicleId);
    }

    public List<Rental> getActiveRentalsByUserId(int userId) throws SQLException {
        return rentalDao.findActiveByUserId(userId);
    }

    public List<Rental> getAllActiveRentals() throws SQLException {
        return rentalDao.findAllActive();
    }

    public List<Rental> getAllRentals(int page, int pageSize) throws SQLException {
        return rentalDao.findAll(page, pageSize);
    }

    public List<Rental> getRentalsByVehicleType(VehicleType vehicleType) throws SQLException {
        return rentalDao.findByVehicleType(vehicleType);
    }

    public List<Rental> getActiveRentals() throws SQLException {
        return rentalDao.findAllActive();
    }

    public void cancelRental(int rentalId) throws SQLException {
        Rental rental = rentalDao.findById(rentalId);
        if (rental == null) {
            throw new SQLException("Kiralama kaydı bulunamadı.");
        }

        if (!rental.isActive()) {
            throw new SQLException("Bu kiralama zaten iptal edilmiş veya tamamlanmış.");
        }

        // Aracın müsaitlik durumunu güncelle
        Vehicle vehicle = vehicleDao.findById(rental.getVehicleId());
        if (vehicle != null) {
            vehicle.setAvailable(true);
            vehicleDao.update(vehicle);
        }

        // Kiralama durumunu güncelle
        rental.setActive(false);
        rentalDao.update(rental);
    }

    public void returnVehicle(int rentalId) throws SQLException {
        Rental rental = rentalDao.findById(rentalId);
        if (rental == null) {
            throw new SQLException("Kiralama kaydı bulunamadı!");
        }

        if (!rental.isActive()) {
            throw new SQLException("Bu kiralama zaten tamamlanmış!");
        }

        // Erken teslim kontrolü ve iade hesaplama
        Date currentDate = new Date(System.currentTimeMillis());
        Date endDate = rental.getEndDate();
        double refundAmount = 0;

        if (currentDate.before(endDate)) {
            // Kalan süreyi hesapla (milisaniye cinsinden)
            long remainingTime = endDate.getTime() - currentDate.getTime();
            long totalTime = endDate.getTime() - rental.getStartDate().getTime();
            
            // Kalan sürenin yüzdesini hesapla
            double remainingPercentage = (double) remainingTime / totalTime;
            
            // İade tutarını hesapla
            refundAmount = rental.getTotalPrice() * remainingPercentage;
        }

        // Aracın müsaitlik durumunu güncelle
        Vehicle vehicle = vehicleDao.findById(rental.getVehicleId());
        if (vehicle != null) {
            vehicle.setAvailable(true);
            vehicleDao.update(vehicle);
        }

        // Kiralama durumunu güncelle
        rental.setActive(false);
        rental.setRefundAmount(refundAmount);
        rental.setEndDate(currentDate); // Bitiş tarihini şu anki tarih olarak güncelle
        rentalDao.update(rental);
    }
} 