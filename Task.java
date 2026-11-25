import java.time.LocalDateTime;

public class Task {
    private int id;
    private String title;
    private int duration;
    private LocalDateTime deadline;
    private String priority;
    private LocalDateTime scheduledStart;
    private LocalDateTime scheduledEnd;

    public Task(int id, String title, int duration, LocalDateTime deadline,
                String priority, LocalDateTime scheduledStart, LocalDateTime scheduledEnd) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.deadline = deadline;
        this.priority = priority;
        this.scheduledStart = scheduledStart;
        this.scheduledEnd = scheduledEnd;
    }

    public Task(String title, int duration, LocalDateTime deadline, String priority) {
        this(0, title, duration, deadline, priority, null, null);
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getDuration() { return duration; }
    public LocalDateTime getDeadline() { return deadline; }
    public String getPriority() { return priority; }
    public LocalDateTime getScheduledStart() { return scheduledStart; }
    public LocalDateTime getScheduledEnd() { return scheduledEnd; }

    public void setId(int id) { this.id = id; }
    public void setScheduledStart(LocalDateTime scheduledStart) { this.scheduledStart = scheduledStart; }
    public void setScheduledEnd(LocalDateTime scheduledEnd) { this.scheduledEnd = scheduledEnd; }

    @Override
    public String toString() {
        return "ID: " + id +
                "\nTask: " + title +
                "\nDuration: " + duration + " minutes" +
                "\nPriority: " + priority +
                "\nDeadline: " + deadline.toString().replace('T', ' ') +
                "\nScheduled start: " + format(scheduledStart) +
                "\nScheduled end: " + format(scheduledEnd);
    }

    private String format(LocalDateTime value) {
        return value == null ? "Not scheduled" : value.toString().replace('T', ' ');
    }
}
