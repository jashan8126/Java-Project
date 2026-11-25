CREATE DATABASE IF NOT EXISTS schedule_optimizer;

USE schedule_optimizer;

CREATE TABLE IF NOT EXISTS tasks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    duration INT NOT NULL,
    deadline DATETIME NOT NULL,
    priority VARCHAR(20) NOT NULL,
    scheduled_start DATETIME NULL,
    scheduled_end DATETIME NULL
);

-- Optional: view saved tasks
SELECT * FROM tasks;
