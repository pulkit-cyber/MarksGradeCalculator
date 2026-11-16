import java.io.*;
import java.util.Scanner;

// PS No. 23 - Marks & Grade Calculator (one student, 5 subjects)
public class MarksGradeCalculator {

    static Scanner sc = new Scanner(System.in);
    static String FILE = "marks.txt";
    static String name = "";
    static int[] marks = new int[5];     // marks stored in subject order
    static boolean hasData = false;      // true once marks are available

    public static void main(String[] args) {
        loadFile();                      // load data at start

        String choice = "";
        while (!choice.equals("6")) {
            System.out.println("\n1. Enter marks   2. Show result   3. Highest/Lowest");
            System.out.println("4. Sort (high to low)   5. Search a mark   6. Exit");
            System.out.print("Choice: ");
            choice = sc.nextLine().trim();

            if (!hasData && choice.matches("[2-5]")) {   // options 2 to 5 need marks
                System.out.println("Enter marks first (option 1).");
                continue;
            }
            switch (choice) {
                case "1": enterMarks();  break;
                case "2": showResult();  break;
                case "3": showHighLow(); break;
                case "4": showSorted();  break;
                case "5": searchMark();  break;
                case "6": break;
                default:  System.out.println("Invalid choice.");
            }
        }
        saveFile();                      // save data on exit
    }

    // Asks until the user enters a whole number from 0 to 100
    static int readMark(String message) {
        while (true) {
            System.out.print(message);
            try {
                int m = Integer.parseInt(sc.nextLine().trim());
                if (m >= 0 && m <= 100) return m;
            } catch (NumberFormatException e) {
                // not a number - falls through to the error message
            }
            System.out.println("Invalid marks. Enter a value between 0 and 100.");
        }
    }

    static void enterMarks() {
        System.out.print("Student name: ");
        name = sc.nextLine().trim();
        for (int i = 0; i < 5; i++) {
            marks[i] = readMark("Marks for Subject " + (i + 1) + ": ");
        }
        hasData = true;
        showResult();
    }

    static int calculateTotal() {
        int total = 0;
        for (int i = 0; i < 5; i++) total += marks[i];
        return total;
    }

    // 90+ = A+, 75-89 = A, 60-74 = B, 40-59 = C, below 40 = F
    static String getGrade(double percent) {
        if (percent >= 90) return "A+";
        if (percent >= 75) return "A";
        if (percent >= 60) return "B";
        if (percent >= 40) return "C";
        return "F";
    }

    static void showResult() {
        int total = calculateTotal();
        double percent = total / 5.0;            // out of 500 -> percentage
        String grade = getGrade(percent);

        String extra = "";
        if (grade.equals("C")) extra = " (Pass)";
        if (grade.equals("F")) extra = " (Fail)";

        System.out.println(name + " | Total: " + total + "/500 | "
                + percent + "% | Grade " + grade + extra);
    }

    static void showHighLow() {
        int high = 0, low = 0;                   // positions in the array
        for (int i = 1; i < 5; i++) {
            if (marks[i] > marks[high]) high = i;
            if (marks[i] < marks[low])  low = i;
        }
        System.out.println("Highest: Subject " + (high + 1) + " (" + marks[high] + ")"
                + " | Lowest: Subject " + (low + 1) + " (" + marks[low] + ")");
    }

    // Bubble sort on a copy, so the subject order of the original stays safe
    static void showSorted() {
        int[] sorted = marks.clone();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4 - i; j++) {
                if (sorted[j] < sorted[j + 1]) {
                    int temp = sorted[j];
                    sorted[j] = sorted[j + 1];
                    sorted[j + 1] = temp;
                }
            }
        }
        for (int i = 0; i < 5; i++) {
            System.out.print(sorted[i] + (i < 4 ? ", " : "\n"));
        }
    }

    // Linear search: check every subject one by one
    static void searchMark() {
        int target = readMark("Search mark: ");
        boolean found = false;
        for (int i = 0; i < 5; i++) {
            if (marks[i] == target) {
                System.out.println("Mark " + target + " found in Subject " + (i + 1));
                found = true;
            }
        }
        if (!found) System.out.println("Mark " + target + " not found.");
    }

    // File format: line 1 = name, line 2 = five marks
    static void loadFile() {
        try {
            Scanner file = new Scanner(new File(FILE));
            name = file.nextLine();
            for (int i = 0; i < 5; i++) marks[i] = file.nextInt();
            file.close();
            hasData = true;
            System.out.println("Saved data loaded for " + name);
        } catch (Exception e) {
            System.out.println("No saved data found. Starting fresh.");
        }
    }

    static void saveFile() {
        if (!hasData) return;
        try {
            PrintWriter out = new PrintWriter(FILE);
            out.println(name);
            for (int i = 0; i < 5; i++) out.print(marks[i] + " ");
            out.close();
            System.out.println("Data saved to " + FILE);
        } catch (FileNotFoundException e) {
            System.out.println("Could not save the file.");
        }
    }
}
