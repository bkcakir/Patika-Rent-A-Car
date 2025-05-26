package Daos;

import Model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private final Connection connection;

    public UserDao(Connection connection) {
        this.connection = connection;
    }

    public User save(User user) throws SQLException {
        String sql = "INSERT INTO users (email, password, role, age, is_corporate, is_active, created_at, updated_at, name, last_name) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setInt(4, user.getAge());
            stmt.setBoolean(5, user.isCorporate());
            stmt.setBoolean(6, user.isActive());
            stmt.setTimestamp(7, new Timestamp(user.getCreatedAt().getTime()));
            stmt.setTimestamp(8, new Timestamp(user.getUpdatedAt().getTime()));
            stmt.setString(9, user.getName());
            stmt.setString(10, user.getLastName());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }
            
            return user;
        }
    }

    public User update(User user) throws SQLException {
        String sql = "UPDATE users SET email = ?, password = ?, role = ?, age = ?, " +
                    "is_corporate = ?, is_active = ?, updated_at = ?, name = ?, last_name = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setInt(4, user.getAge());
            stmt.setBoolean(5, user.isCorporate());
            stmt.setBoolean(6, user.isActive());
            stmt.setTimestamp(7, new Timestamp(user.getUpdatedAt().getTime()));
            stmt.setString(8, user.getName());
            stmt.setString(9, user.getLastName());
            stmt.setInt(10, user.getId());
            
            stmt.executeUpdate();
            return user;
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setAge(rs.getInt("age"));
        user.setCorporate(rs.getBoolean("is_corporate"));
        user.setActive(rs.getBoolean("is_active"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        user.setName(rs.getString("name"));
        user.setLastName(rs.getString("last_name"));
        return user;
    }
} 