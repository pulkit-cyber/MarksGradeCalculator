import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

/*
 * Marks & Grade Calculator  (PS No. 23)
 * School of Computer Science and Engineering, Rungta International Skills University
 *
 * Console application that calculates the result of ONE student in 5 subjects.
 * Concepts used: Array, Sorting, Searching, Methods, File Handling.
 *
 * Grade scale : 90+ = A+, 75-89 = A, 60-74 = B, 40-59 = C, below 40 = F
 * Storage     : marks.txt  ->  StudentName|m1,m2,m3,m4,m5   (subject order)
 *
 * Compile : javac MarksGradeCalculator.java
 * Run     : java MarksGradeCalculator
 */

/* ------------------------------------------------------------------ */
/* Student : holds the name and the 5 subject marks (in subject order) */
/* ------------------------------------------------------------------ */
class Student {
    private final String name;
    private final int[] marks;

    public Student(String name, int[] marks) {
        this.name = name;
        this.marks = marks.clone();
    }

    public String getName() {
        return name;
    }

    // A copy is returned so that sorting never disturbs the subject order.
    public int[] getMarks() {
        return marks.clone();
    }
}

/* ------------------------------------------------------------------ */
/* GradeCalculator : all calculations, sorting, searching and file I/O */
/* ------------------------------------------------------------------ */
class GradeCalculator {
    public static final int SUBJECTS = 5;
    public static final int MAX_MARK = 100;
    private static final String FILE_NAME = "marks.txt";

    private Student student;   // null until marks are entered or loaded

    public GradeCalculator() {
        student = null;
    }

    public boolean hasStudent() {
        return student != null;
    }

    public void setStudent(String name, int[] marks) {
        student = new Student(name, marks);
    }

    // Marks must lie between 0 and 100 (both included).
    public static boolean isValidMark(int mark) {
        return mark >= 0 && mark <= MAX_MARK;
    }

    // ---------------- Calculations (loops over the array) ----------------

    public int calculateTotal() {
        int[] marks = student.getMarks();
        int total = 0;
        for (int i = 0; i < marks.length; i++) {
            total += marks[i];
        }
        return total;
    }

    public double calculatePercentage() {
        return (calculateTotal() * 100.0) / (SUBJECTS * MAX_MARK);
    }

    public String getGrade() {
        double percentage = calculatePercentage();
        if (percentage >= 90) {
            return "A+";
        } else if (percentage >= 75) {
            return "A";
        } else if (percentage >= 60) {
            return "B";
        } else if (percentage >= 40) {
            return "C";
        } else {
            return "F";
        }
    }

    // ---------------- Features shown through the menu ----------------

    public void showResult() {
        String grade = getGrade();
        String remark = "";
        if (grade.equals("C")) {
            remark = " (Pass)";     // lowest passing grade is flagged
        } else if (grade.equals("F")) {
            remark = " (Fail)";
        }
        System.out.println(student.getName()
                + " | Total: " + calculateTotal() + "/" + (SUBJECTS * MAX_MARK)
                + " | " + String.format(Locale.US, "%.1f", calculatePercentage()) + "%"
                + " | Grade " + grade + remark);
    }

    public void showHighestLowest() {
        int[] marks = student.getMarks();
        int high = findHighestIndex(marks);
        int low = findLowestIndex(marks);
        System.out.println("Highest: Subject " + (high + 1) + " (" + marks[high] + ")"
                + " | Lowest: Subject " + (low + 1) + " (" + marks[low] + ")");
    }

    public void showSortedMarks() {
        int[] sorted = sortDescending(student.getMarks());
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < sorted.length; i++) {
            if (i > 0) {
                line.append(", ");
            }
            line.append(sorted[i]);
        }
        System.out.println(line);
    }

    // Linear search: every subject holding the given mark is reported.
    public void searchMark(int mark) {
        int[] marks = student.getMarks();
        StringBuilder found = new StringBuilder();
        int count = 0;
        for (int i = 0; i < marks.length; i++) {
            if (marks[i] == mark) {
                if (count > 0) {
                    found.append(", ");
                }
                found.append("Subject ").append(i + 1);
                count++;
            }
        }
        if (count == 0) {
            System.out.println("Mark " + mark + " not found.");
        } else {
            System.out.println("Mark " + mark + " found in " + found);
        }
    }

    // ---------------- Helper methods (array work) ----------------

    // Returns the position of the first highest mark.
    private int findHighestIndex(int[] marks) {
        int index = 0;
        for (int i = 1; i < marks.length; i++) {
            if (marks[i] > marks[index]) {
                index = i;
            }
        }
        return index;
    }

    // Returns the position of the first lowest mark.
    private int findLowestIndex(int[] marks) {
        int index = 0;
        for (int i = 1; i < marks.length; i++) {
            if (marks[i] < marks[index]) {
                index = i;
            }
        }
        return index;
    }

    // Selection sort (high to low) on the array that is passed in.
    private int[] sortDescending(int[] marks) {
        for (int i = 0; i < marks.length - 1; i++) {
            int max = i;
            for (int j = i + 1; j < marks.length; j++) {
                if (marks[j] > marks[max]) {
                    max = j;
                }
            }
            int temp = marks[max];
            marks[max] = marks[i];
            marks[i] = temp;
        }
        return marks;
    }

    // ---------------- File handling ----------------

    public void saveToFile() {
        if (student == null) {
            return;                         // nothing to save
        }
        int[] marks = student.getMarks();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            StringBuilder line = new StringBuilder(student.getName()).append("|");
            for (int i = 0; i < marks.length; i++) {
                if (i > 0) {
                    line.append(",");
                }
                line.append(marks[i]);
            }
            writer.write(line.toString());
            writer.newLine();
            System.out.println("Data saved to " + FILE_NAME + ".");
        } catch (IOException e) {
            System.out.println("Could not save data: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;                         // first run: no saved data yet
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line == null) {
                return;
            }
            String[] parts = line.split("\\|");
            if (parts.length != 2 || parts[0].trim().isEmpty()) {
                throw new NumberFormatException("bad record");
            }
            String[] values = parts[1].split(",");
            if (values.length != SUBJECTS) {
                throw new NumberFormatException("wrong number of marks");
            }
            int[] marks = new int[SUBJECTS];
            for (int i = 0; i < SUBJECTS; i++) {
                marks[i] = Integer.parseInt(values[i].trim());
                if (!isValidMark(marks[i])) {
                    throw new NumberFormatException("mark out of range");
                }
            }
            student = new Student(parts[0].trim(), marks);
            System.out.println("Loaded saved record for " + student.getName()
                    + " from " + FILE_NAME + ".");
        } catch (NumberFormatException e) {
            System.out.println(FILE_NAME + " has invalid data. Starting with no record.");
        } catch (IOException e) {
            System.out.println("Could not read " + FILE_NAME + ": " + e.getMessage());
        }
    }
}

/* ------------------------------------------------------------------ */
/* Main class : menu-driven console interface                          */
/* ------------------------------------------------------------------ */
public class MarksGradeCalculator {

    private static final int END_OF_INPUT = -1;
    private static final String LINE = "--------------------------------------------";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        GradeCalculator calc = new GradeCalculator();

        calc.loadFromFile();                // load data at start

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Enter your choice: ");
            if (!sc.hasNextLine()) {
                break;                      // input ended
            }
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    enterMarks(sc, calc);
                    break;
                case "2":
                    if (checkData(calc)) {
                        calc.showResult();
                    }
                    break;
                case "3":
                    if (checkData(calc)) {
                        calc.showHighestLowest();
                    }
                    break;
                case "4":
                    if (checkData(calc)) {
                        calc.showSortedMarks();
                    }
                    break;
                case "5":
                    if (checkData(calc)) {
                        int mark = readMark(sc, "Enter mark to search: ");
                        if (mark != END_OF_INPUT) {
                            calc.searchMark(mark);
                        }
                    }
                    break;
                case "6":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Enter a number between 1 and 6.");
            }
            if (running) {
                System.out.println(LINE);
            }
        }

        calc.saveToFile();                  // save data on exit
        sc.close();
    }

    private static void printMenu() {
        System.out.println("====== Marks & Grade Calculator ======");
        System.out.println("1. Enter Marks");
        System.out.println("2. Show Result");
        System.out.println("3. Highest / Lowest Subject");
        System.out.println("4. Sort Marks (High to Low)");
        System.out.println("5. Search a Mark");
        System.out.println("6. Exit");
        System.out.println(LINE);
    }

    private static boolean checkData(GradeCalculator calc) {
        if (!calc.hasStudent()) {
            System.out.println("No marks entered yet. Choose option 1 first.");
            return false;
        }
        return true;
    }

    // Option 1: read the name and 5 marks, then show the result.
    private static void enterMarks(Scanner sc, GradeCalculator calc) {
        String name = "";
        while (name.isEmpty()) {
            System.out.print("Enter student name: ");
            if (!sc.hasNextLine()) {
                return;
            }
            name = sc.nextLine().replace("|", " ").trim();
            if (name.isEmpty()) {
                System.out.println("Name cannot be blank.");
            }
        }

        int[] marks = new int[GradeCalculator.SUBJECTS];
        for (int i = 0; i < marks.length; i++) {
            marks[i] = readMark(sc, "Enter marks for Subject " + (i + 1) + " (0-100): ");
            if (marks[i] == END_OF_INPUT) {
                return;                     // nothing is stored if input stops midway
            }
        }

        calc.setStudent(name, marks);
        calc.showResult();
    }

    // Keeps asking until a whole number from 0 to 100 is entered.
    private static int readMark(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!sc.hasNextLine()) {
                return END_OF_INPUT;
            }
            String text = sc.nextLine().trim();
            try {
                int value = Integer.parseInt(text);
                if (GradeCalculator.isValidMark(value)) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // not a number: fall through to the error message
            }
            System.out.println("Invalid marks. Enter a value between 0 and 100.");
        }
    }
}
