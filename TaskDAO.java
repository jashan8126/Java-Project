import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TaskDAO {

    public int addTask(Task task) throws SQLException {
        String sql = "INSERT INTO tasks (title, duration, deadline, priority) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, task.getTitle());
            ps.setInt(2, task.getDuration());
            ps.setTimestamp(3, Timestamp.valueOf(task.getDeadline()));
            ps.setString(4, task.getPriority());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }

    public ArrayList<Task> getAllTasks() throws SQLException {
        ArrayList<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks ORDER BY id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Timestamp start = rs.getTimestamp("scheduled_start");
                Timestamp end = rs.getTimestamp("scheduled_end");

                tasks.add(new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getInt("duration"),
                        rs.getTimestamp("deadline").toLocalDateTime(),
                        rs.getString("priority"),
                        start == null ? null : start.toLocalDateTime(),
                        end == null ? null : end.toLocalDateTime()
                ));
            }
        }
        return tasks;
    }

    public boolean deleteTask(int id) throws SQLException {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateSchedule(int id, LocalDateTime start, LocalDateTime end) throws SQLException {
        String sql = "UPDATE tasks SET scheduled_start = ?, scheduled_end = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean clearSchedule() throws SQLException {
        String sql = "UPDATE tasks SET scheduled_start = NULL, scheduled_end = NULL";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.executeUpdate();
            return true;
        }
    }
}
