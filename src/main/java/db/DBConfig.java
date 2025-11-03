package db;

public final class DBConfig {
    private DBConfig() {}
    public static final String URL  = "jdbc:mysql://localhost:3306/srms_db?useSSL=false&serverTimezone=UTC";
    public static final String USER = "root";     // XAMPP default
    public static final String PASS = "";         // empty by default
}
