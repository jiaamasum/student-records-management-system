package repo;


import db.DBConnection;
import exception.DataAccessException;
import model.Student;

import java.sql.*;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDAOImpl implements StudentDAO {

    private Student map(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("department"),
                rs.getDouble("gpa"),
                rs.getTimestamp("created_at").toInstant().atZone(ZoneId.systemDefault()).toInstant()
        );
    }

    @Override
    public Student save(Student s) {
        final String sql = "INSERT INTO students(name,email,department,gpa) VALUES(?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getDepartment());
            ps.setDouble(4, s.getGpa());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) s.setId(keys.getInt(1));
            }
            return s;
        } catch (SQLException e) {
            throw new DataAccessException("Insert failed", e);
        }
    }

    @Override
    public Student update(Student s) {
        final String sql = "UPDATE students SET name=?, email=?, department=?, gpa=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getDepartment());
            ps.setDouble(4, s.getGpa());
            ps.setInt(5, s.getId());
            ps.executeUpdate();
            return s;
        } catch (SQLException e) {
            throw new DataAccessException("Update failed", e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        final String sql = "DELETE FROM students WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Delete failed", e);
        }
    }

    @Override
    public Optional<Student> findById(Integer id) {
        final String sql = "SELECT * FROM students WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Find by id failed", e);
        }
    }

    @Override
    public List<Student> findAll() {
        final String sql = "SELECT * FROM students";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Student> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Find all failed", e);
        }
    }

    @Override
    public boolean emailExists(String email) {
        final String sql = "SELECT 1 FROM students WHERE email=? LIMIT 1";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Email check failed", e);
        }
    }

    @Override
    public List<Student> searchByName(String nameLike) {
        final String sql = "SELECT * FROM students WHERE name LIKE ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + nameLike + "%");
            try (ResultSet rs = ps.executeQuery()) {
                List<Student> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Search failed", e);
        }
    }

    @Override
    public List<Student> sortBy(String field, boolean asc) {
        String col = switch (field.toLowerCase()) {
            case "name" -> "name";
            case "gpa" -> "gpa";
            case "department" -> "department";
            default -> "id";
        };
        final String sql = "SELECT * FROM students ORDER BY " + col + (asc ? " ASC" : " DESC");
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Student> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Sort failed", e);
        }
    }
}

