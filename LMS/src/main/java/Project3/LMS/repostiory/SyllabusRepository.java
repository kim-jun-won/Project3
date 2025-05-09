package Project3.LMS.repostiory;

import Project3.LMS.domain.Syllabus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SyllabusRepository extends JpaRepository<Syllabus, Long> {
    Syllabus findByCourseId(Long courseId);
}
