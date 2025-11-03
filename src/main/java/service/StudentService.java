package service;


import exception.DuplicateEmailException;
import exception.NotFoundException;
import model.Student;
import repo.StudentDAO;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class StudentService {
    private final StudentDAO dao;
    private final Map<Integer, Student> cache = new ConcurrentHashMap<>();
    private final ReentrantLock writeLock = new ReentrantLock(); // synchronize writes

    public StudentService(StudentDAO dao) { this.dao = dao; }

    public Student addStudent(Student s) {
        if (dao.emailExists(s.getEmail())) throw new DuplicateEmailException(s.getEmail());
        writeLock.lock();
        try {
            Student saved = dao.save(s);
            cache.put(saved.getId(), saved);
            return saved;
        } finally {
            writeLock.unlock();
        }
    }

    public Student updateStudent(Student s) {
        if (s.getId() == null) throw new NotFoundException("ID required");
        writeLock.lock();
        try {
            Student updated = dao.update(s);
            cache.put(updated.getId(), updated);
            return updated;
        } finally {
            writeLock.unlock();
        }
    }

    public void delete(int id) {
        writeLock.lock();
        try {
            dao.deleteById(id);
            cache.remove(id);
        } finally {
            writeLock.unlock();
        }
    }

    public Student getById(int id) {
        return cache.computeIfAbsent(id, i ->
                dao.findById(i).orElseThrow(() -> new NotFoundException("Student not found: " + i)));
    }

    public List<Student> all() {
        List<Student> list = dao.findAll();
        list.forEach(s -> cache.putIfAbsent(s.getId(), s));
        return list;
    }

    // Streams: search + sort in-memory
    public List<Student> searchByName(String q) {
        return all().stream()
                .filter(s -> s.getName().toLowerCase().contains(q.toLowerCase()))
                .collect(Collectors.toList());
    }
    public List<Student> sortBy(String field, boolean asc) {
        Comparator<Student> cmp = switch (field.toLowerCase()) {
            case "name" -> Comparator.comparing(Student::getName);
            case "gpa" -> Comparator.comparingDouble(Student::getGpa);
            case "department" -> Comparator.comparing(Student::getDepartment);
            default -> Comparator.comparing(Student::getId);
        };
        if (!asc) cmp = cmp.reversed();
        return all().stream().sorted(cmp).collect(Collectors.toList());
    }

    // Simple multi-threading demo: run tasks concurrently (reads safe, writes locked)
    public void simulateConcurrentAccess() {
        ExecutorService pool = Executors.newFixedThreadPool(3);
        List<Callable<Void>> jobs = List.of(
                () -> { all(); return null; },
                () -> { searchByName("a"); return null; },
                () -> { if (!all().isEmpty()) { // synchronized write section
                    writeLock.lock();
                    try {
                        Student s = all().get(0);
                        s.setGpa(Math.min(4.0, s.getGpa() + 0.1));
                        dao.update(s); cache.put(s.getId(), s);
                    } finally { writeLock.unlock(); }
                } return null; }
        );
        try { pool.invokeAll(jobs); } catch (InterruptedException ignored) { }
        finally { pool.shutdown(); }
    }
}
