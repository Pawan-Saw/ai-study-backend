package ai_study_backend.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import ai_study_backend.model.Task;
import ai_study_backend.repository.TaskRepository;
import ai_study_backend.util.JwtUtil;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin
public class TaskController {

    private final TaskRepository repo;
    private final JwtUtil jwtUtil;

    public TaskController(TaskRepository repo, JwtUtil jwtUtil) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
    }

    // ✅ Email extract karo token se
    private String getEmail(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtUtil.extractEmail(token);
    }

    // ✅ GET — Sirf apni tasks
    @GetMapping
    public List<Task> getTasks(@RequestHeader("Authorization") String authHeader) {
        String email = getEmail(authHeader);
        return repo.findByUserEmail(email);
    }

    // ✅ POST — Task save with email
    @PostMapping
    public Task addTask(@RequestHeader("Authorization") String authHeader,
                        @RequestBody Task task) {
        String email = getEmail(authHeader);
        task.setUserEmail(email);
        return repo.save(task);
    }

    // ✅ PUT — Edit task
    @PutMapping("/{id}")
    public Task updateTask(@RequestHeader("Authorization") String authHeader,
                           @PathVariable Long id,
                           @RequestBody Task updated) {
        String email = getEmail(authHeader);
        Task task = repo.findById(id).orElseThrow();
        if (!task.getUserEmail().equals(email)) {
            throw new RuntimeException("Unauthorized!");
        }
        task.setText(updated.getText());
        task.setDone(updated.isDone());
        task.setPriority(updated.getPriority());
        task.setDueDate(updated.getDueDate());
        task.setCategory(updated.getCategory());
        return repo.save(task);
    }

    // ✅ DELETE — Sirf apni task delete karo
    @DeleteMapping("/{id}")
    public String deleteTask(@RequestHeader("Authorization") String authHeader,
                             @PathVariable Long id) {
        String email = getEmail(authHeader);
        Task task = repo.findById(id).orElseThrow();
        if (!task.getUserEmail().equals(email)) {
            throw new RuntimeException("Unauthorized!");
        }
        repo.deleteById(id);
        return "Deleted";
    }
}