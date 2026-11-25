import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

public class TaskManager {
    private final TaskDAO taskDAO;

    public TaskManager() {
        taskDAO = new TaskDAO();
    }

    public void addTask(Task task) throws SQLException {
        int id = taskDAO.addTask(task);
        if (id > 0) {
            System.out.println("Task added successfully. Task ID: " + id);
        } else {
            System.out.println("Task could not be added.");
        }
    }

    public ArrayList<Task> getTasks() throws SQLException {
        return taskDAO.getAllTasks();
    }

    public void displayTasks() throws SQLException {
        ArrayList<Task> tasks = getTasks();

        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }

        System.out.println("\n--------------- ALL TASKS ---------------");
        for (Task task : tasks) {
            System.out.println("-----------------------------------------");
            System.out.println(task);
        }
        System.out.println("-----------------------------------------");
    }

    public boolean deleteTask(int id) throws SQLException {
        return taskDAO.deleteTask(id);
    }

    public void generateSchedule(LocalDateTime dayStart) throws SQLException {
        ArrayList<Task> tasks = getTasks();

        if (tasks.isEmpty()) {
            System.out.println("Add some tasks before generating a schedule.");
            return;
        }

        // Higher priority comes first. For equal priority, the earlier deadline comes first.
        tasks.sort(Comparator
                .comparingInt((Task t) -> priorityValue(t.getPriority()))
                .thenComparing(Task::getDeadline));

        taskDAO.clearSchedule();
        LocalDateTime current = dayStart;

        System.out.println("\n============== DAILY SCHEDULE ==============");
        for (Task task : tasks) {
            LocalDateTime end = current.plusMinutes(task.getDuration());
            taskDAO.updateSchedule(task.getId(), current, end);

            System.out.println("\nTask ID: " + task.getId());
            System.out.println("Task: " + task.getTitle());
            System.out.println("Priority: " + task.getPriority());
            System.out.println("Start: " + current.toString().replace('T', ' '));
            System.out.println("End:   " + end.toString().replace('T', ' '));

            if (end.isAfter(task.getDeadline())) {
                System.out.println("WARNING: This task will finish after its deadline.");
            } else {
                System.out.println("Deadline: On time");
            }

            current = end;
        }
        System.out.println("\nSchedule generated and saved in MySQL.");
        System.out.println("============================================");
    }

    public void delayTask(int taskId, int delayMinutes) throws SQLException {
        if (delayMinutes <= 0) {
            System.out.println("Delay must be greater than zero.");
            return;
        }

        ArrayList<Task> tasks = getTasks();
        Task delayedTask = null;

        for (Task task : tasks) {
            if (task.getId() == taskId) {
                delayedTask = task;
                break;
            }
        }

        if (delayedTask == null) {
            System.out.println("Task ID not found.");
            return;
        }

        if (delayedTask.getScheduledStart() == null || delayedTask.getScheduledEnd() == null) {
            System.out.println("Generate a schedule before delaying a task.");
            return;
        }

        LocalDateTime delayedEnd = delayedTask.getScheduledEnd().plusMinutes(delayMinutes);
        taskDAO.updateSchedule(taskId, delayedTask.getScheduledStart(), delayedEnd);

        // Shift every task scheduled after the delayed task by the same delay.
        for (Task task : tasks) {
            if (task.getId() != taskId && task.getScheduledStart() != null
                    && !task.getScheduledStart().isBefore(delayedTask.getScheduledEnd())) {
                LocalDateTime newStart = task.getScheduledStart().plusMinutes(delayMinutes);
                LocalDateTime newEnd = task.getScheduledEnd().plusMinutes(delayMinutes);
                taskDAO.updateSchedule(task.getId(), newStart, newEnd);
            }
        }

        System.out.println("Schedule recalculated. Following tasks were shifted by "
                + delayMinutes + " minutes.");
        checkDeadlineWarnings();
    }

    public void detectConflicts() throws SQLException {
        ArrayList<Task> tasks = getTasks();
        boolean found = false;

        System.out.println("\n========== SCHEDULING CONFLICTS ==========");
        for (int i = 0; i < tasks.size(); i++) {
            Task first = tasks.get(i);
            if (first.getScheduledStart() == null || first.getScheduledEnd() == null) {
                continue;
            }

            for (int j = i + 1; j < tasks.size(); j++) {
                Task second = tasks.get(j);
                if (second.getScheduledStart() == null || second.getScheduledEnd() == null) {
                    continue;
                }

                boolean overlap = first.getScheduledStart().isBefore(second.getScheduledEnd())
                        && second.getScheduledStart().isBefore(first.getScheduledEnd());

                if (overlap) {
                    found = true;
                    System.out.println("Overlap detected:");
                    System.out.println(first.getTitle() + " and " + second.getTitle());
                }
            }

            if (first.getScheduledEnd().isAfter(first.getDeadline())) {
                found = true;
                System.out.println("Deadline conflict: " + first.getTitle()
                        + " finishes after its deadline.");
            }
        }

        if (!found) {
            System.out.println("No scheduling or deadline conflicts found.");
        }
        System.out.println("==========================================");
    }

    private void checkDeadlineWarnings() throws SQLException {
        ArrayList<Task> tasks = getTasks();
        for (Task task : tasks) {
            if (task.getScheduledEnd() != null && task.getScheduledEnd().isAfter(task.getDeadline())) {
                System.out.println("Deadline warning: " + task.getTitle()
                        + " is now scheduled after its deadline.");
            }
        }
    }

    private int priorityValue(String priority) {
        switch (priority.toUpperCase()) {
            case "HIGH": return 1;
            case "MEDIUM": return 2;
            default: return 3;
        }
    }
}
