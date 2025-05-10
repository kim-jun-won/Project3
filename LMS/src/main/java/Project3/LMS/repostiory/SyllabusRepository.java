package Project3.LMS.repostiory;

import Project3.LMS.domain.Syllabus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SyllabusRepository extends JpaRepository<Syllabus, Long> {
    Optional<Syllabus> findByCourseId(Long courseId);

}
