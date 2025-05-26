package Daos;

import Db.DatabaseConnection;
import Model.Vehicle;
import Model.VehicleType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDao {
    private final Connection connection;

    public VehicleDao(Connection connection) {
        this.connection = connection;
    }

    public Vehicle save(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicles (brand, model, year, vehicle_type, daily_price, price, " +
                    "is_available, plate, is_active, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, vehicle.getBrand());
            stmt.setString(2, vehicle.getModel());
            stmt.setInt(3, vehicle.getYear());
            stmt.setString(4, vehicle.getVehicleType().toString());
            stmt.setDouble(5, vehicle.getDailyPrice());
            stmt.setDouble(6, vehicle.getPrice());
            stmt.setBoolean(7, vehicle.isAvailable());
            stmt.setString(8, vehicle.getPlate());
            stmt.setBoolean(9, vehicle.isActive());
            stmt.setTimestamp(10, new Timestamp(vehicle.getCreatedAt().getTime()));
            stmt.setTimestamp(11, new Timestamp(vehicle.getUpdatedAt().getTime()));
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    vehicle.setId(rs.getInt(1));
                }
            }
            
            return vehicle;
        }
    }

    public Vehicle update(Vehicle vehicle) throws SQLException {
        String sql = "UPDATE vehicles SET brand = ?, model = ?, year = ?, vehicle_type = ?, " +
                    "daily_price = ?, price = ?, is_available = ?, plate = ?, " +
                    "is_active = ?, updated_at = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, vehicle.getBrand());
            stmt.setString(2, vehicle.getModel());
            stmt.setInt(3, vehicle.getYear());
            stmt.setString(4, vehicle.getVehicleType().toString());
            stmt.setDouble(5, vehicle.getDailyPrice());
            stmt.setDouble(6, vehicle.getPrice());
            stmt.setBoolean(7, vehicle.isAvailable());
            stmt.setString(8, vehicle.getPlate());
            stmt.setBoolean(9, vehicle.isActive());
            stmt.setTimestamp(10, new Timestamp(vehicle.getUpdatedAt().getTime()));
            stmt.setInt(11, vehicle.getId());
            
            stmt.executeUpdate();
            return vehicle;
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM vehicles WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Vehicle findById(int id) throws SQLException {
        String sql = "SELECT * FROM vehicles WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVehicle(rs);
                }
            }
        }
        return null;
    }

    public List<Vehicle> findAll() throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        }
        return vehicles;
    }

    public List<Vehicle> findAvailable() throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE is_available = true AND is_active = true";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        }
        return vehicles;
    }

    public List<Vehicle> getAllVehicles() throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE is_active = true";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        }
        return vehicles;
    }

    public List<Vehicle> getVehiclesByCategory(VehicleType vehicleType) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE vehicle_type = ? AND is_active = true";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, vehicleType.toString());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(mapResultSetToVehicle(rs));
                }
            }
        }
        return vehicles;
    }

    public Vehicle getVehicleById(int id) throws SQLException {
        String sql = "SELECT * FROM vehicles WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVehicle(rs);
                }
            }
        }
        return null;
    }

    public void updateVehicleCategories() throws SQLException {
        String sql = "UPDATE vehicles SET vehicle_type = CASE " +
                    "WHEN vehicle_type = 'Station Wagon' THEN 'STATION_WAGON' " +
                    "WHEN vehicle_type = 'Spor' THEN 'SPOR' " +
                    "WHEN vehicle_type = 'Sedan' THEN 'SEDAN' " +
                    "WHEN vehicle_type = 'Suv' THEN 'SUV' " +
                    "WHEN vehicle_type = 'Hatchback' THEN 'HATCHBACK' " +
                    "ELSE vehicle_type END";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(rs.getInt("id"));
        vehicle.setBrand(rs.getString("brand"));
        vehicle.setModel(rs.getString("model"));
        vehicle.setYear(rs.getInt("year"));
        
        String vehicleTypeStr = rs.getString("vehicle_type").toUpperCase()
            .replace("İ", "I")  // Türkçe karakter düzeltmesi
            .replace("STATİONWAGON", "STATION_WAGON")
            .replace("STATIONWAGON", "STATION_WAGON");
            
        vehicle.setVehicleType(VehicleType.valueOf(vehicleTypeStr));
        vehicle.setDailyPrice(rs.getDouble("daily_price"));
        vehicle.setPrice(rs.getDouble("price"));
        vehicle.setAvailable(rs.getBoolean("is_available"));
        vehicle.setPlate(rs.getString("plate"));
        vehicle.setActive(rs.getBoolean("is_active"));
        vehicle.setCreatedAt(rs.getTimestamp("created_at"));
        vehicle.setUpdatedAt(rs.getTimestamp("updated_at"));
        return vehicle;
    }

    public void addVehicle(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicles (brand, model, year, vehicle_type, daily_price, price, " +
                    "is_available, plate, is_active, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, vehicle.getBrand());
            stmt.setString(2, vehicle.getModel());
            stmt.setInt(3, vehicle.getYear());
            stmt.setString(4, vehicle.getVehicleType().toString());
            stmt.setDouble(5, vehicle.getDailyPrice());
            stmt.setDouble(6, vehicle.getPrice());
            stmt.setBoolean(7, vehicle.isAvailable());
            stmt.setString(8, vehicle.getPlate());
            stmt.setBoolean(9, vehicle.isActive());
            stmt.setTimestamp(10, new Timestamp(vehicle.getCreatedAt().getTime()));
            stmt.setTimestamp(11, new Timestamp(vehicle.getUpdatedAt().getTime()));
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    vehicle.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void updateVehicle(Vehicle vehicle) throws SQLException {
        String sql = "UPDATE vehicles SET brand = ?, model = ?, year = ?, vehicle_type = ?, daily_price = ?, price = ?, is_available = ?, plate = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, vehicle.getBrand());
            stmt.setString(2, vehicle.getModel());
            stmt.setInt(3, vehicle.getYear());
            stmt.setString(4, vehicle.getVehicleType().name());
            stmt.setDouble(5, vehicle.getDailyPrice());
            stmt.setDouble(6, vehicle.getPrice());
            stmt.setBoolean(7, vehicle.isAvailable());
            stmt.setString(8, vehicle.getPlate());
            stmt.setInt(9, vehicle.getId());
            stmt.executeUpdate();
        }
    }
} 