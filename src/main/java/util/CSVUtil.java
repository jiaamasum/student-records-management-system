package util;

import model.Student;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class CSVUtil {
    private CSVUtil() {}

    public static void exportCSV(List<Student> students, File file) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {
            bw.write("id,name,email,department,gpa\n");
            for (Student s : students) {
                bw.write(String.format("%s,%s,%s,%s,%.2f%n",
                        s.getId() == null ? "" : s.getId(),
                        escape(s.getName()), escape(s.getEmail()),
                        escape(s.getDepartment()), s.getGpa()));
            }
        }
    }

    public static List<Student> importCSV(File file) throws IOException {
        List<Student> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line; br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] a = split(line);
                if (a.length < 5) continue;
                String name = unescape(a[1]);
                String email = unescape(a[2]);
                String dept = unescape(a[3]);
                double gpa = Double.parseDouble(a[4]);
                list.add(new Student(name, email, dept, gpa));
            }
        }
        return list;
    }

    // very tiny CSV helpers (handles commas inside quotes)
    private static String[] split(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean q = false;
        for (char c : line.toCharArray()) {
            if (c == '"') q = !q;
            else if (c == ',' && !q) { out.add(sb.toString()); sb.setLength(0); }
            else sb.append(c);
        }
        out.add(sb.toString());
        return out.toArray(new String[0]);
    }
    private static String escape(String s) {
        if (s.contains(",") || s.contains("\"")) return "\"" + s.replace("\"","\"\"") + "\"";
        return s;
    }
    private static String unescape(String s) {
        if (s.startsWith("\"") && s.endsWith("\"")) return s.substring(1, s.length()-1).replace("\"\"","\"");
        return s;
    }
}
