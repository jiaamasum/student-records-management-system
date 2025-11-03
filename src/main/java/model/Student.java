package model;

import java.time.Instant;
import java.util.Objects;

public class Student {
    private Integer id;           // null for new records
    private String  name;
    private String  email;
    private String  department;
    private double  gpa;
    private Instant createdAt;    // read-only from DB

    public Student(Integer id, String name, String email, String department, double gpa, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.gpa = gpa;
        this.createdAt = createdAt;
    }

    public Student(String name, String email, String department, double gpa) {
        this(null, name, email, department, gpa, null);
    }

    // Getters/setters (encapsulation)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }
    public Instant getCreatedAt() { return createdAt; }

    @Override public String toString() {
        return "Student{id=%d, name='%s', email='%s', dept='%s', gpa=%.2f}"
                .formatted(id, name, email, department, gpa);
    }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student s)) return false;
        return Objects.equals(id, s.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}

