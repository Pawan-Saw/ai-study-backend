package ai_study_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ai_study_backend.model.Task;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    // ✅ Sirf us user ki tasks fetch karo
    List<Task> findByUserEmail(String userEmail);
}