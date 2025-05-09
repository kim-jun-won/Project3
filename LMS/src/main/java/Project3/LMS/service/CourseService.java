package Project3.LMS.service;

import Project3.LMS.domain.Course;
import Project3.LMS.repostiory.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

    /**
     * 교수 ID로 담당 과목 전체 조회
     */
    public List<Course> getCoursesByProfessor(Long professorId) {
        return courseRepository.findByProfessorId(professorId);
    }

    /**
     * 과목 ID로 단일 과목 조회
     */
    public Course getCourse(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 과목을 찾을 수 없습니다."));
    }
}
