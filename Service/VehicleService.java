package Service;

import Daos.VehicleDao;
import Model.Vehicle;
import Model.VehicleType;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class VehicleService {
    private final VehicleDao vehicleDao;

    public VehicleService(VehicleDao vehicleDao) {
        this.vehicleDao = vehicleDao;
    }

    public void addVehicle(Vehicle vehicle) throws SQLException {
        vehicle.setActive(true);
        vehicle.setCreatedAt(new Date());
        vehicle.setUpdatedAt(new Date());
        vehicleDao.save(vehicle);
    }

    public List<Vehicle> getAllVehicles() throws SQLException {
        return vehicleDao.getAllVehicles();
    }

    public List<Vehicle> getVehiclesByCategory(VehicleType vehicleType) throws SQLException {
        return vehicleDao.getVehiclesByCategory(vehicleType);
    }

    public Vehicle getVehicleById(int id) throws SQLException {
        return vehicleDao.findById(id);
    }

    public List<Vehicle> getAvailableVehicles() throws SQLException {
        return vehicleDao.findAvailable();
    }

    public Vehicle updateVehicle(Vehicle vehicle) throws SQLException {
        Vehicle existingVehicle = vehicleDao.findById(vehicle.getId());
        if (existingVehicle == null) {
            throw new SQLException("Araç bulunamadı.");
        }
        return vehicleDao.update(vehicle);
    }

    public void deleteVehicle(int id) throws SQLException {
        Vehicle vehicle = vehicleDao.findById(id);
        if (vehicle == null) {
            throw new SQLException("Araç bulunamadı.");
        }
        vehicleDao.delete(id);
    }

    public Vehicle updateAvailability(int vehicleId, boolean available) throws SQLException {
        Vehicle vehicle = vehicleDao.findById(vehicleId);
        if (vehicle == null) {
            throw new SQLException("Araç bulunamadı.");
        }
        vehicle.setAvailable(available);
        return vehicleDao.update(vehicle);
    }
} 