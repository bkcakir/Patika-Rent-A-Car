package Daos;

import Db.DatabaseConnection;
import Model.Rental;
import Model.User;
import Model.Vehicle;
import Model.VehicleType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RentalDao {
    private final Connection connection;

    public RentalDao(Connection connection) {
        this.connection = connection;
    }

    public Rental save(Rental rental, int duration) throws SQLException {
        // Başlangıç tarihini 00:00 olarak ayarla
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(rental.getStartDate());
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        rental.setStartDate(startCalendar.getTime());

        // Bitiş tarihini hesapla
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(startCalendar.getTime());
        
        switch (rental.getRentalType()) {
            case "SAATLIK":
                endCalendar.add(Calendar.HOUR_OF_DAY, duration); // Kullanıcının girdiği saat kadar
                endCalendar.set(Calendar.MINUTE, 0);
                endCalendar.set(Calendar.SECOND, 0);
                break;
            case "GUNLUK":
                endCalendar.add(Calendar.DAY_OF_MONTH, duration); // Kullanıcının girdiği gün kadar
                endCalendar.set(Calendar.HOUR_OF_DAY, 23);
                endCalendar.set(Calendar.MINUTE, 59);
                endCalendar.set(Calendar.SECOND, 59);
                break;
            case "HAFTALIK":
                endCalendar.add(Calendar.DAY_OF_MONTH, duration * 7); // Kullanıcının girdiği hafta kadar
                endCalendar.set(Calendar.HOUR_OF_DAY, 23);
                endCalendar.set(Calendar.MINUTE, 59);
                endCalendar.set(Calendar.SECOND, 59);
                break;
            case "AYLIK":
                endCalendar.add(Calendar.DAY_OF_MONTH, duration * 30); // Kullanıcının girdiği ay kadar
                endCalendar.set(Calendar.HOUR_OF_DAY, 23);
                endCalendar.set(Calendar.MINUTE, 59);
                endCalendar.set(Calendar.SECOND, 59);
                break;
        }
        rental.setEndDate(endCalendar.getTime());
        rental.setDuration(duration);
        rental.setActive(true); // Kiralama aktif olarak başlar

        String sql = "INSERT INTO rentals (user_id, vehicle_id, start_date, end_date, " +
                    "total_price, deposit_amount, rental_type, duration, is_active, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, rental.getUserId());
            stmt.setInt(2, rental.getVehicleId());
            stmt.setTimestamp(3, new Timestamp(rental.getStartDate().getTime()));
            stmt.setTimestamp(4, new Timestamp(rental.getEndDate().getTime()));
            stmt.setDouble(5, rental.getTotalPrice());
            stmt.setDouble(6, rental.getDepositAmount());
            stmt.setString(7, rental.getRentalType());
            stmt.setInt(8, duration);
            stmt.setBoolean(9, true); // Kiralama aktif olarak başlar
            stmt.setTimestamp(10, new Timestamp(rental.getCreatedAt().getTime()));
            stmt.setTimestamp(11, new Timestamp(rental.getUpdatedAt().getTime()));
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    rental.setId(rs.getInt(1));
                }
            }
            
            return rental;
        }
    }

    public Rental update(Rental rental) throws SQLException {
        String sql = "UPDATE rentals SET user_id = ?, vehicle_id = ?, start_date = ?, " +
                    "end_date = ?, total_price = ?, deposit_amount = ?, rental_type = ?, " +
                    "is_active = ?, updated_at = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, rental.getUserId());
            stmt.setInt(2, rental.getVehicleId());
            stmt.setTimestamp(3, new Timestamp(rental.getStartDate().getTime()));
            stmt.setTimestamp(4, new Timestamp(rental.getEndDate().getTime()));
            stmt.setDouble(5, rental.getTotalPrice());
            stmt.setDouble(6, rental.getDepositAmount());
            stmt.setString(7, rental.getRentalType());
            stmt.setBoolean(8, rental.isActive());
            stmt.setTimestamp(9, new Timestamp(rental.getUpdatedAt().getTime()));
            stmt.setInt(10, rental.getId());
            
            stmt.executeUpdate();
            return rental;
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM rentals WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Rental findById(int id) throws SQLException {
        String sql = "SELECT r.*, u.email, u.name, u.last_name, v.plate, v.brand, v.model " +
                    "FROM rentals r " +
                    "LEFT JOIN users u ON r.user_id = u.id " +
                    "LEFT JOIN vehicles v ON r.vehicle_id = v.id " +
                    "WHERE r.id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Rental rental = mapResultSetToRental(rs);
                    
                    // Kiralama durumunu end_date'e göre kontrol et
                    Date currentDate = new Date(System.currentTimeMillis());
                    rental.setActive(currentDate.before(rental.getEndDate()));
                    
                    return rental;
                }
            }
        }
        return null;
    }

    public List<Rental> findAll() throws SQLException {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT r.*, u.email, u.name, u.last_name, v.plate, v.brand, v.model " +
                    "FROM rentals r " +
                    "LEFT JOIN users u ON r.user_id = u.id " +
                    "LEFT JOIN vehicles v ON r.vehicle_id = v.id";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Rental rental = mapResultSetToRental(rs);
                
                // User bilgilerini set et
                User user = new User();
                user.setEmail(rs.getString("email"));
                user.setName(rs.getString("name") + " " + rs.getString("last_name"));
                rental.setUser(user);

                // Vehicle bilgilerini set et
                Vehicle vehicle = new Vehicle();
                vehicle.setPlate(rs.getString("plate"));
                vehicle.setBrand(rs.getString("brand"));
                vehicle.setModel(rs.getString("model"));
                rental.setVehicle(vehicle);

                rentals.add(rental);
            }
        }
        return rentals;
    }

    public List<Rental> findByUserId(int userId) throws SQLException {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT r.*, u.email, u.name, u.last_name, v.plate, v.brand, v.model " +
                    "FROM rentals r " +
                    "LEFT JOIN users u ON r.user_id = u.id " +
                    "LEFT JOIN vehicles v ON r.vehicle_id = v.id " +
                    "WHERE r.user_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Rental rental = mapResultSetToRental(rs);
                    
                    // User bilgilerini set et
                    User user = new User();
                    user.setEmail(rs.getString("email"));
                    user.setName(rs.getString("name") + " " + rs.getString("last_name"));
                    rental.setUser(user);

                    // Vehicle bilgilerini set et
                    Vehicle vehicle = new Vehicle();
                    vehicle.setPlate(rs.getString("plate"));
                    vehicle.setBrand(rs.getString("brand"));
                    vehicle.setModel(rs.getString("model"));
                    rental.setVehicle(vehicle);

                    rentals.add(rental);
                }
            }
        }
        return rentals;
    }

    public List<Rental> findByVehicleId(int vehicleId) throws SQLException {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rentals WHERE vehicle_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rentals.add(mapResultSetToRental(rs));
                }
            }
        }
        return rentals;
    }

    private Rental mapResultSetToRental(ResultSet rs) throws SQLException {
        Rental rental = new Rental();
        rental.setId(rs.getInt("id"));
        rental.setUserId(rs.getInt("user_id"));
        rental.setVehicleId(rs.getInt("vehicle_id"));
        rental.setStartDate(rs.getTimestamp("start_date"));
        rental.setEndDate(rs.getTimestamp("end_date"));
        rental.setTotalPrice(rs.getDouble("total_price"));
        rental.setDepositAmount(rs.getDouble("deposit_amount"));
        rental.setRentalType(rs.getString("rental_type"));
        rental.setActive(rs.getBoolean("is_active"));
        rental.setCreatedAt(rs.getTimestamp("created_at"));
        rental.setUpdatedAt(rs.getTimestamp("updated_at"));
        rental.setDuration(rs.getInt("duration"));
        rental.setRefundAmount(rs.getDouble("refund_amount"));

        // Araç bilgilerini set et
        Vehicle vehicle = new Vehicle();
        vehicle.setId(rs.getInt("vehicle_id"));
        vehicle.setPlate(rs.getString("plate"));
        vehicle.setBrand(rs.getString("brand"));
        vehicle.setModel(rs.getString("model"));
        rental.setVehicle(vehicle);

        // Kullanıcı bilgilerini set et
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setName(rs.getString("name") + " " + rs.getString("last_name"));
        rental.setUser(user);

        return rental;
    }

    public List<Rental> findActiveByVehicleId(int vehicleId) throws SQLException {
        List<Rental> rentals = new ArrayList<>();
        String query = "SELECT * FROM rentals WHERE vehicle_id = ? AND end_date >= CURRENT_DATE ORDER BY start_date DESC";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, vehicleId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Rental rental = new Rental();
                rental.setId(rs.getInt("id"));
                rental.setUserId(rs.getInt("user_id"));
                rental.setVehicleId(rs.getInt("vehicle_id"));
                rental.setStartDate(rs.getTimestamp("start_date"));
                rental.setEndDate(rs.getTimestamp("end_date"));
                rental.setTotalPrice(rs.getDouble("total_price"));
                rental.setDepositAmount(rs.getDouble("deposit_amount"));
                rental.setRentalType(rs.getString("rental_type"));
                rental.setActive(rs.getBoolean("is_active"));
                rental.setCreatedAt(rs.getTimestamp("created_at"));
                rental.setUpdatedAt(rs.getTimestamp("updated_at"));
                rentals.add(rental);
            }
        }
        return rentals;
    }

    public List<Rental> findActiveByUserId(int userId) throws SQLException {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT r.*, u.email, u.name, u.last_name, v.plate, v.brand, v.model " +
                    "FROM rentals r " +
                    "LEFT JOIN users u ON r.user_id = u.id " +
                    "LEFT JOIN vehicles v ON r.vehicle_id = v.id " +
                    "WHERE r.user_id = ? AND r.end_date >= CURRENT_DATE";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Rental rental = mapResultSetToRental(rs);
                    
                    // User bilgilerini set et
                    User user = new User();
                    user.setEmail(rs.getString("email"));
                    user.setName(rs.getString("name") + " " + rs.getString("last_name"));
                    rental.setUser(user);

                    // Vehicle bilgilerini set et
                    Vehicle vehicle = new Vehicle();
                    vehicle.setPlate(rs.getString("plate"));
                    vehicle.setBrand(rs.getString("brand"));
                    vehicle.setModel(rs.getString("model"));
                    rental.setVehicle(vehicle);

                    rentals.add(rental);
                }
            }
        }
        return rentals;
    }

    public List<Rental> findAllActive() throws SQLException {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT r.*, u.email, u.name, u.last_name, v.plate, v.brand, v.model " +
                    "FROM rentals r " +
                    "LEFT JOIN users u ON r.user_id = u.id " +
                    "LEFT JOIN vehicles v ON r.vehicle_id = v.id " +
                    "WHERE r.is_active = true AND r.end_date >= CURRENT_TIMESTAMP";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Rental rental = mapResultSetToRental(rs);
                
                // User bilgilerini set et
                User user = new User();
                user.setEmail(rs.getString("email"));
                user.setName(rs.getString("name") + " " + rs.getString("last_name"));
                rental.setUser(user);

                // Vehicle bilgilerini set et
                Vehicle vehicle = new Vehicle();
                vehicle.setPlate(rs.getString("plate"));
                vehicle.setBrand(rs.getString("brand"));
                vehicle.setModel(rs.getString("model"));
                rental.setVehicle(vehicle);

                rentals.add(rental);
            }
        }
        return rentals;
    }

    public List<Rental> findAll(int page, int pageSize) throws SQLException {
        List<Rental> rentals = new ArrayList<>();
        String query = "SELECT * FROM rentals ORDER BY start_date DESC LIMIT ? OFFSET ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, pageSize);
            stmt.setInt(2, (page - 1) * pageSize);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Rental rental = new Rental();
                rental.setId(rs.getInt("id"));
                rental.setUserId(rs.getInt("user_id"));
                rental.setVehicleId(rs.getInt("vehicle_id"));
                rental.setStartDate(rs.getTimestamp("start_date"));
                rental.setEndDate(rs.getTimestamp("end_date"));
                rental.setTotalPrice(rs.getDouble("total_price"));
                rental.setDepositAmount(rs.getDouble("deposit_amount"));
                rental.setRentalType(rs.getString("rental_type"));
                rental.setActive(rs.getBoolean("is_active"));
                rental.setCreatedAt(rs.getTimestamp("created_at"));
                rental.setUpdatedAt(rs.getTimestamp("updated_at"));
                rentals.add(rental);
            }
        }
        return rentals;
    }

    public List<Rental> findByCategory(VehicleType vehicleType) throws SQLException {
        List<Rental> rentals = new ArrayList<>();
        String query = "SELECT r.* FROM rentals r JOIN vehicles v ON r.vehicle_id = v.id WHERE v.vehicle_type = ? ORDER BY r.start_date DESC";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, vehicleType.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Rental rental = new Rental();
                rental.setId(rs.getInt("id"));
                rental.setUserId(rs.getInt("user_id"));
                rental.setVehicleId(rs.getInt("vehicle_id"));
                rental.setStartDate(rs.getTimestamp("start_date"));
                rental.setEndDate(rs.getTimestamp("end_date"));
                rental.setTotalPrice(rs.getDouble("total_price"));
                rental.setDepositAmount(rs.getDouble("deposit_amount"));
                rental.setRentalType(rs.getString("rental_type"));
                rental.setActive(rs.getBoolean("is_active"));
                rental.setCreatedAt(rs.getTimestamp("created_at"));
                rental.setUpdatedAt(rs.getTimestamp("updated_at"));
                rentals.add(rental);
            }
        }
        return rentals;
    }

    public List<Rental> findByVehicleType(VehicleType vehicleType) throws SQLException {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT r.*, u.email, u.name, u.last_name, v.plate, v.brand, v.model " +
                    "FROM rentals r " +
                    "LEFT JOIN users u ON r.user_id = u.id " +
                    "LEFT JOIN vehicles v ON r.vehicle_id = v.id " +
                    "WHERE v.vehicle_type = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, vehicleType.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Rental rental = mapResultSetToRental(rs);
                    rentals.add(rental);
                }
            }
        }
        return rentals;
    }

    public String getRemainingTime(Rental rental) {
        Date now = new Date();
        Date endDate = rental.getEndDate();
        
        if (endDate.before(now)) {
            return "Süre doldu";
        }
        
        long diffInMillis = endDate.getTime() - now.getTime();
        
        switch (rental.getRentalType()) {
            case "SAATLIK":
                long hours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis) % 60;
                return String.format("%d saat %d dakika kaldı", hours, minutes);
                
            case "GUNLUK":
                long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);
                long remainingHours = TimeUnit.MILLISECONDS.toHours(diffInMillis) % 24;
                return String.format("%d gün %d saat kaldı", days, remainingHours);
                
            case "HAFTALIK":
                long weeks = TimeUnit.MILLISECONDS.toDays(diffInMillis) / 7;
                long remainingDays = TimeUnit.MILLISECONDS.toDays(diffInMillis) % 7;
                return String.format("%d hafta %d gün kaldı", weeks, remainingDays);
                
            case "AYLIK":
                long months = TimeUnit.MILLISECONDS.toDays(diffInMillis) / 30;
                long remainingDaysInMonth = TimeUnit.MILLISECONDS.toDays(diffInMillis) % 30;
                return String.format("%d ay %d gün kaldı", months, remainingDaysInMonth);
                
            default:
                return "Geçersiz kiralama tipi";
        }
    }

    public List<Rental> findAllWithRemainingTime() throws SQLException {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT r.id, r.user_id, r.vehicle_id, r.start_date, r.end_date, " +
                    "r.total_price, r.deposit_amount, r.rental_type, r.is_active, " +
                    "r.created_at, r.updated_at, u.email, u.name, u.last_name, v.plate " +
                    "FROM rentals r " +
                    "LEFT JOIN users u ON r.user_id = u.id " +
                    "LEFT JOIN vehicles v ON r.vehicle_id = v.id " +
                    "WHERE r.is_active = true";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Rental rental = mapResultSetToRental(rs);
                
                // User bilgilerini set et
                User user = new User();
                user.setEmail(rs.getString("email"));
                user.setName(rs.getString("name") + " " + rs.getString("last_name"));
                rental.setUser(user);

                // Vehicle bilgilerini set et
                Vehicle vehicle = new Vehicle();
                vehicle.setPlate(rs.getString("plate"));
                rental.setVehicle(vehicle);

                // Kalan süreyi hesapla ve set et
                String remainingTime = getRemainingTime(rental);
                rental.setRemainingTime(remainingTime);

                rentals.add(rental);
            }
        }
        return rentals;
    }
} 