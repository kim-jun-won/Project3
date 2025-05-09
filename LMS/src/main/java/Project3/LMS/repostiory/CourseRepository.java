package Project3.LMS.repostiory;

import Project3.LMS.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // 교수 ID로 담당 과목 리스트 조회
    List<Course> findByProfessorId(Long professorId);

    // 과목 이름으로 검색
    List<Course> findByCourseNameContaining(String keyword);
}
