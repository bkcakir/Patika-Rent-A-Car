package Service;

import Model.User;
import Daos.UserDao;
import Helper.PasswordHelper;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User register(String email, String password, String role, int age, boolean isCorporate, String name, String lastName) throws SQLException {
        if (userDao.findByEmail(email) != null) {
            throw new SQLException("Bu email adresi zaten kullanılıyor.");
        }

        String hashedPassword = PasswordHelper.createHashedPassword(password);
        User user = new User(email, hashedPassword, role, age, isCorporate, name, lastName);
        return userDao.save(user);
    }

    public User login(String email, String password) throws SQLException {
        User user = userDao.findByEmail(email);
        if (user == null) {
            System.out.println("Kullanıcı bulunamadı: " + email);
            return null;
        }
        
        boolean passwordVerified = PasswordHelper.verifyPassword(password, user.getPassword());
        System.out.println("Şifre doğrulama sonucu: " + passwordVerified);
        
        if (passwordVerified) {
            return user;
        }
        return null;
    }

    public List<User> getAllUsers() throws SQLException {
        return userDao.findAll();
    }

    public User getUserById(int id) throws SQLException {
        return userDao.findById(id);
    }

    public User updateUser(User user) throws SQLException {
        User existingUser = userDao.findById(user.getId());
        if (existingUser == null) {
            throw new SQLException("Kullanıcı bulunamadı.");
        }
        return userDao.update(user);
    }

    public void deleteUser(int id) throws SQLException {
        User user = userDao.findById(id);
        if (user == null) {
            throw new SQLException("Kullanıcı bulunamadı.");
        }
        userDao.delete(id);
    }

    public User updateUserRole(int userId, String newRole) throws SQLException {
        User user = userDao.findById(userId);
        if (user == null) {
            throw new SQLException("Kullanıcı bulunamadı.");
        }
        user.setRole(newRole);
        return userDao.update(user);
    }

    public User registerUser(String email, String password, String role, int age, boolean isCorporate, String name, String lastName) throws SQLException {
        if (userDao.findByEmail(email) != null) {
            throw new SQLException("Bu email adresi zaten kullanımda!");
        }

        User user = new User(email, password, role, age, isCorporate, name, lastName);
        return userDao.save(user);
    }
} 