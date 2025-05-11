package Project3.LMS.repostiory;

import Project3.LMS.domain.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // 특정 학생이 수강 중인 모든 과목 조회
    List<Enrollment> findByStudentId(Long studentId);

    boolean existsByStudentIdAndCourseId(Long id, Long courseId);
}