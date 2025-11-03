
import exception.DuplicateEmailException;
import exception.NotFoundException;
import model.Student;
import repo.StudentDAOImpl;
import service.StudentService;
import util.CSVUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class App {
    private static final Scanner in = new Scanner(System.in);
    private static final StudentService service = new StudentService(new StudentDAOImpl());

    public static void main(String[] args) {
        System.out.println("=== Student Records Management System (SRMS) ===");
        boolean run = true;
        while (run) {
            menu();
            String choice = in.nextLine().trim();
            switch (choice) {
                case "1" -> add();
                case "2" -> update();
                case "3" -> delete();
                case "4" -> list();
                case "5" -> search();
                case "6" -> sort();
                case "7" -> exportCSV();
                case "8" -> importCSV();
                case "9" -> simulate();
                case "0" -> run = false;
                default -> System.out.println("Invalid option.");
            }
        }
        System.out.println("Bye!");
    }

    private static void menu() {
        System.out.println("""
                
                1) Add Student
                2) Update Student
                3) Delete Student
                4) View All
                5) Search by Name
                6) Sort (name/gpa/department)
                7) Export CSV
                8) Import CSV
                9) Simulate Concurrent Access
                0) Exit
                Choose: """);
    }

    private static void add() {
        try {
            System.out.print("Name: ");        String name = in.nextLine().trim();
            System.out.print("Email: ");       String email = in.nextLine().trim();
            System.out.print("Department: ");  String dept = in.nextLine().trim();
            System.out.print("GPA (0-4): ");   double gpa = Double.parseDouble(in.nextLine().trim());
            Student saved = service.addStudent(new Student(name, email, dept, gpa));
            System.out.println("Saved: " + saved);
        } catch (DuplicateEmailException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    private static void update() {
        try {
            System.out.print("ID: "); int id = Integer.parseInt(in.nextLine().trim());
            Student s = service.getById(id);
            System.out.print("New Name (" + s.getName() + "): "); String name = emptyDefault(in.nextLine(), s.getName());
            System.out.print("New Email (" + s.getEmail() + "): "); String email = emptyDefault(in.nextLine(), s.getEmail());
            System.out.print("New Dept (" + s.getDepartment() + "): "); String dept = emptyDefault(in.nextLine(), s.getDepartment());
            System.out.print("New GPA (" + s.getGpa() + "): "); String g = in.nextLine().trim();
            double gpa = g.isEmpty() ? s.getGpa() : Double.parseDouble(g);

            s.setName(name); s.setEmail(email); s.setDepartment(dept); s.setGpa(gpa);
            System.out.println("Updated: " + service.updateStudent(s));
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    private static void delete() {
        try {
            System.out.print("ID: "); int id = Integer.parseInt(in.nextLine().trim());
            service.delete(id);
            System.out.println("Deleted.");
        } catch (Exception e) { System.out.println("Failed."); }
    }

    private static void list() {
        List<Student> list = service.all();
        if (list.isEmpty()) System.out.println("No data.");
        else list.forEach(System.out::println);
    }

    private static void search() {
        System.out.print("Search name: ");
        service.searchByName(in.nextLine().trim()).forEach(System.out::println);
    }

    private static void sort() {
        System.out.print("Field (name/gpa/department) & order (asc/desc): ");
        String[] a = in.nextLine().trim().split("\\s+");
        String field = a.length > 0 ? a[0] : "id";
        boolean asc = a.length < 2 || a[1].equalsIgnoreCase("asc");
        service.sortBy(field, asc).forEach(System.out::println);
    }

    private static void exportCSV() {
        System.out.print("File path to export (e.g., students.csv): ");
        File f = new File(in.nextLine().trim());
        try {
            CSVUtil.exportCSV(service.all(), f);
            System.out.println("Exported to: " + f.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Export failed: " + e.getMessage());
        }
    }

    private static void importCSV() {
        System.out.print("CSV path to import: ");
        File f = new File(in.nextLine().trim());
        try {
            var list = CSVUtil.importCSV(f);
            list.forEach(s -> {
                try { service.addStudent(s); }
                catch (DuplicateEmailException ex) { /* skip duplicates */ }
            });
            System.out.println("Imported " + list.size() + " rows (existing emails skipped).");
        } catch (IOException e) {
            System.out.println("Import failed: " + e.getMessage());
        }
    }

    private static void simulate() {
        System.out.println("Running concurrent tasks...");
        service.simulateConcurrentAccess();
        System.out.println("Done.");
    }

    private static String emptyDefault(String input, String def) {
        input = input.trim();
        return input.isEmpty() ? def : input;
    }
}

