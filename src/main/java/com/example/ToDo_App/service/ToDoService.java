package com.example.ToDo_App.service;

import com.example.ToDo_App.model.ToDo;
import com.example.ToDo_App.repo.IToDoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ToDoService {

    private static final String STATUS_ALL = "All";
    private static final String STATUS_TODO = "ToDo";
    private static final String STATUS_DOING = "Doing";
    private static final String STATUS_DONE = "Done";
    private static final String REPEAT_NONE = "/";

    @Autowired
    private IToDoRepo repo;

    @Autowired
    private JavaMailSender mailSender;

    // Get all ToDo items
    public List<ToDo> getAllToDoItems() {
        // repo.findAll() već vraća List, nema potrebe za ArrayList + lambda
        return repo.findAll();
    }

    // Get a ToDo item by its ID
    public ToDo getToDoItemById(Long id) {
        return repo.findById(id).orElse(null);
    }

    // Update status to "Done" and handle repeatable tasks
    public boolean updateStatus(Long id) {
        ToDo originalTask = getToDoItemById(id);
        if (originalTask == null) {
            return false;
        }

        if (STATUS_DOING.equals(originalTask.getStatus())) {
            originalTask.setDoneTime(new Date());
        }

        originalTask.setStatus(STATUS_DONE);
        repo.save(originalTask);

        // sada se metoda stvarno koristi (PMD: unused private method rešeno)
        sendCompletionEmail(originalTask);

        // Process repeatable task: Create a new task if needed
        if (originalTask.getRepeatFrequency() != null && !REPEAT_NONE.equals(originalTask.getRepeatFrequency())) {
            createNewRepeatableTask(originalTask);
        }

        return true;
    }

    // Create a new task with updated due date for repeatable tasks
    private void createNewRepeatableTask(ToDo originalTask) {
        Calendar calendar = Calendar.getInstance();
        Date baseDate = originalTask.getNextDueDate() != null ? originalTask.getNextDueDate() : originalTask.getDate();
        calendar.setTime(baseDate);

        // Switch statements should be exhaustive -> dodajemo default
        switch (originalTask.getRepeatFrequency()) {
            case "Daily":
                calendar.add(Calendar.DATE, 1);
                break;
            case "Weekly":
                calendar.add(Calendar.DATE, 7);
                break;
            case "Monthly":
                calendar.add(Calendar.MONTH, 1);
                break;
            case "Yearly":
                calendar.add(Calendar.YEAR, 1);
                break;
            default:
                // ako dođe neočekivana vrednost, ne pravimo novu nalogu
                return;
        }

        ToDo newTask = new ToDo();
        newTask.setTitle(originalTask.getTitle());
        newTask.setDate(calendar.getTime());
        newTask.setTime(originalTask.getTime());
        newTask.setStatus(STATUS_TODO);
        newTask.setRepeatFrequency(originalTask.getRepeatFrequency());
        newTask.setNextDueDate(calendar.getTime());

        repo.save(newTask);
    }

    // Fetch repeatable tasks
    public List<ToDo> getRepeatableTasks(String status) {
        List<ToDo> tasks = getToDoItemsByStatus(status);

        return tasks.stream()
                .filter(task -> task.getRepeatFrequency() != null &&
                        ("Daily".equalsIgnoreCase(task.getRepeatFrequency()) ||
                                "Weekly".equalsIgnoreCase(task.getRepeatFrequency()) ||
                                "Monthly".equalsIgnoreCase(task.getRepeatFrequency()) ||
                                "Yearly".equalsIgnoreCase(task.getRepeatFrequency())))
                .collect(Collectors.toList());
    }

    // Save or update a ToDo item
    public boolean saveOrUpdateToDoItem(ToDo todo) {
        ToDo updatedObj = repo.save(todo);
        return repo.findById(updatedObj.getId()).isPresent();
    }

    // Delete a ToDo item
    public boolean deleteToDoItem(Long id) {
        repo.deleteById(id);
        return repo.findById(id).isEmpty();
    }

    // Get ToDo items filtered by status
    public List<ToDo> getToDoItemsByStatus(String status) {
        // PMD: Position literals first in String comparisons
        return STATUS_ALL.equals(status) ? repo.findAll() : repo.findByStatus(status);
    }

    // Send email notification for completed task
    private void sendCompletionEmail(ToDo todo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("recipient@example.com");
        message.setSubject("Task Completed");
        message.setText("The task '" + todo.getTitle() + "' has been marked as completed.");
        mailSender.send(message);
    }

    // Calculate number of tasks by status
    public long getTaskCountByStatus(String status) {
        return repo.findByStatus(status).size();
    }

    // Calculate total number of tasks
    public long getTotalTaskCount() {
        return repo.count();
    }

    public double calculateAverageDoingTime() {
        List<ToDo> doingTasks = repo.findByStatus(STATUS_DOING);
        if (doingTasks.isEmpty()) {
            return 0.0;
        }

        return doingTasks.stream()
                .mapToLong(task -> Duration.between(task.getTime(), LocalTime.now()).toMinutes())
                .average()
                .orElse(0.0);
    }

    // Calculate percentage of "Done" tasks
    public double calculateDoneTaskPercentage() {
        long totalTasks = getTotalTaskCount();
        if (totalTasks == 0) {
            return 0.0;
        }

        long doneTasks = getTaskCountByStatus(STATUS_DONE);

        // PMD: Useless parentheses uklonjeno
        return doneTasks * 100.0 / totalTasks;
    }
}
