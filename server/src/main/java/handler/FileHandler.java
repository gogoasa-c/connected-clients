package handler;

import common.Logger;
import common.Student;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static java.lang.reflect.Modifier.isStatic;

public class FileHandler {
    private static final Logger log = Logger.getLogger();

    public static void saveStudentsToFile(final List<Student> students) {
        String fileName = generateFileName();
        try (FileWriter writer = new FileWriter(fileName)) {
            writeAttendanceFile(writer, students);
            log.info("Student data saved to " + fileName);
        } catch (IOException | IllegalAccessException e) {
            log.error("Failed to save student data to file: " + e.getMessage());
        }
    }

    private static int getNonStaticFieldCount(Field[] fields) {
        int nonStaticFieldCount = 0;
        for (Field field : fields) {
            if (!isStatic(field.getModifiers())) {
                nonStaticFieldCount++;
            }
        }
        return nonStaticFieldCount;
    }

    private static String getKeyValueFieldPair(Field field, Student student) throws IllegalAccessException {
        return String.format("\"%s\": \"%s\"", field.getName(), field.get(student));
    }

    private static void writeStudentObjectToFile(FileWriter writer, List<Student> students,
                                                 int studentIndex) throws IOException, IllegalAccessException {
        Student student = students.get(studentIndex);

        writer.write("  {");
        Field[] fields = student.getClass().getDeclaredFields();
        int nonStaticFieldCount = getNonStaticFieldCount(fields);
        int currentField = 0;
        for (Field field : fields) {
            if (!isStatic(field.getModifiers())) {
                field.setAccessible(true);
                writer.write(getKeyValueFieldPair(field, student));
                currentField++;
                if (currentField < nonStaticFieldCount) {
                    writer.write(", ");
                }
            }
        }
        writer.write("}");

        if (studentIndex < students.size() - 1) {
            writer.write(",");
        }
        writer.write("\n");
    }

    private static void writeAttendanceFile(FileWriter writer, List<Student> students) throws IOException,
            IllegalAccessException {
        writer.write("[\n");
        for (int i = 0; i < students.size(); i++) {
            writeStudentObjectToFile(writer, students, i);
        }
        writer.write("]");
    }

    private static String generateFileName() {
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        return "attendance_" + date + ".json";
    }
}
