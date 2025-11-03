package repo;

import model.Student;
import java.util.List;

public interface StudentDAO extends CrudRepository<Student, Integer> {
    boolean emailExists(String email);
    List<Student> searchByName(String nameLike);     // searching
    List<Student> sortBy(String field, boolean asc); // sorting (name, gpa, department)
}