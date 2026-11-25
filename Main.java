import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        // Test the database connection before showing the menu.
        try {
            DBConnection.getConnection().close();
            System.out.println("MySQL connected successfully.");
        } catch (SQLException e) {
            System.out.println("Could not connect to MySQL.");
            System.out.println("Check DBConnection.java username, password and database setup.");
            System.out.println("Error: " + e.getMessage());
            return;
        }

        int choice;
        do {
            showMenu();
            choice = readInt("Enter your choice: ");

            try {
                switch (choice) {
                    case 1:
                        addTask(manager);
                        break;
                    case 2:
                        manager.displayTasks();
                        break;
                    case 3:
                        LocalDateTime start = readDateTime(
                                "Enter schedule start date and time (yyyy-MM-dd HH:mm): ");
                        manager.generateSchedule(start);
                        break;
                    case 4:
                        int id = readInt("Enter task ID to delay: ");
                        int delay = readInt("Enter delay in minutes: ");
                        manager.delayTask(id, delay);
                        break;
                    case 5:
                        manager.detectConflicts();
                        break;
                    case 6:
                        int deleteId = readInt("Enter task ID to delete: ");
                        if (manager.deleteTask(deleteId)) {
                            System.out.println("Task deleted successfully.");
                        } else {
                            System.out.println("Task ID not found.");
                        }
                        break;
                    case 7:
                        System.out.println("Thank you for using Intelligent Daily Schedule Optimizer.");
                        break;
                    default:
                        System.out.println("Invalid choice. Enter a number from 1 to 7.");
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            }
        } while (choice != 7);
    }

    private static void showMenu() {
        System.out.println("\n==============================================");
        System.out.println("     INTELLIGENT DAILY SCHEDULE OPTIMIZER");
        System.out.println("==============================================");
        System.out.println("1. Add New Task");
        System.out.println("2. View All Tasks");
        System.out.println("3. Generate Daily Schedule");
        System.out.println("4. Delay a Task and Recalculate");
        System.out.println("5. Detect Scheduling Conflicts");
        System.out.println("6. Delete Task");
        System.out.println("7. Exit");
        System.out.println("==============================================");
    }

    private static void addTask(TaskManager manager) throws SQLException {
        System.out.print("Enter task title: ");
        String title = sc.nextLine().trim();

        if (title.isEmpty()) {
            System.out.println("Task title cannot be empty.");
            return;
        }

        int duration = readInt("Enter estimated duration in minutes: ");
        if (duration <= 0) {
            System.out.println("Duration must be greater than zero.");
            return;
        }

        LocalDateTime deadline = readDateTime("Enter deadline (yyyy-MM-dd HH:mm): ");

        System.out.print("Enter priority (HIGH/MEDIUM/LOW): ");
        String priority = sc.nextLine().trim().toUpperCase();

        if (!priority.equals("HIGH") && !priority.equals("MEDIUM")
                && !priority.equals("LOW")) {
            System.out.println("Priority must be HIGH, MEDIUM or LOW.");
            return;
        }

        manager.addTask(new Task(title, duration, deadline, priority));
    }

    private static int readInt(String message) {
        System.out.print(message);
        String value = sc.nextLine().trim();
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please enter a valid whole number.");
        }
    }

    private static LocalDateTime readDateTime(String message) {
        System.out.print(message);
        String value = sc.nextLine().trim();
        try {
            return LocalDateTime.parse(value, FORMAT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Use this format: yyyy-MM-dd HH:mm");
        }
    }
}
