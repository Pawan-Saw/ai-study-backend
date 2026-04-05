package ai_study_backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;
    private boolean done;
    private String priority;
    private LocalDate dueDate;
    private String category;

    // ✅ USER EMAIL — kis user ki task hai
    private String userEmail;

    public Task() {}

    public Long getId() { return id; }
    public String getText() { return text; }
    public boolean isDone() { return done; }
    public String getPriority() { return priority; }
    public LocalDate getDueDate() { return dueDate; }
    public String getCategory() { return category; }
    public String getUserEmail() { return userEmail; }

    public void setText(String text) { this.text = text; }
    public void setDone(boolean done) { this.done = done; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setCategory(String category) { this.category = category; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
}