import java.sql.*;

public class DatabaseManager {
    private Connection conn;

    public DatabaseManager() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:alumnos.db");
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS alumnos (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "nombre TEXT NOT NULL, " +
                     "curso TEXT NOT NULL)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void agregarAlumno(String nombre, String curso) {
        String sql = "INSERT INTO alumnos (nombre, curso) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, curso);
            pstmt.executeUpdate();
            System.out.println("Alumno agregado correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void mostrarAlumnos() {
        String sql = "SELECT * FROM alumnos";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " - " + rs.getString("nombre") + " - " + rs.getString("curso"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return conn;
    }
}
