package Project3.LMS.repostiory;

import Project3.LMS.domain.AssignmentFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentFileRepository extends JpaRepository<AssignmentFile, Long> {
}