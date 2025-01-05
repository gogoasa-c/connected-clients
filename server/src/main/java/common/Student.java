package common;

public record Student(int id, String name) {
    private static int idCounter = 0;

    public Student(String name) {
        this(++idCounter, name);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}