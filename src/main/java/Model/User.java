package Model;

public class User extends BaseModel {
    private int id;
    private String email;
    private String password;
    private String role; // ADMIN, MUSTERI, KURUMSAL
    private int age;
    private boolean isCorporate;
    private String name;
    private String lastName;

    public User(String email, String password, String role, int age, boolean isCorporate, String name, String lastName) {
        super();
        this.email = email;
        this.password = password;
        this.role = role;
        this.age = age;
        this.isCorporate = isCorporate;
        this.name = name;
        this.lastName = lastName;
    }

    public User() {
        super();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isCorporate() {
        return isCorporate;
    }

    public void setCorporate(boolean corporate) {
        isCorporate = corporate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        String line = "+-----+------------+------+------------+------------+------------+\n";
        String header = String.format(
            "| %-3s | %-10s | %-4s | %-10s | %-10s | %-10s |\n",
            "ID", "Email", "Yaş", "Rol", "Kurumsal", "Şifre"
        );
        String data = String.format(
            "| %-3d | %-10s | %-4d | %-10s | %-10s | %-10s |\n",
            id,
            email,
            age,
            role,
            isCorporate ? "Evet" : "Hayır",
            "********"
        );

        return line + header + line + data + line;
    }
} 