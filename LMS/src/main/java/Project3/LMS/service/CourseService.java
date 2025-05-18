package Project3.LMS.service;


import Project3.LMS.domain.Course;
import Project3.LMS.domain.Enrollment;
import Project3.LMS.repostiory.CourseRepository;
import Project3.LMS.repostiory.CourseSearch;
import Project3.LMS.repostiory.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    //==강의 등록==//
    @Transactional
    public Long registerCourse(Course course) {

        validateDuplicateCourse(course);
        courseRepository.save(course);
        return course.getId();
    }



    private void validateDuplicateCourse(Course course) {
        List <Course> findCourses = courseRepository.findByName(course.getCourseName());

        if(!findCourses.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 강의명입니다.");
        }

    }

    //==강의 삭제==//
    @Transactional
    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findOne(courseId);
        courseRepository.delete(course);
    }

    public List<Course> findCourses(CourseSearch courseSearch) {
        return courseRepository.findAll(courseSearch); // 여기서 repository 메서드를 호출
    }

    public Course findbyCourseId(Long courseId) {
        return courseRepository.findById(courseId);
    }

    public List<Course> getCoursesByProfessor(Long id) {
        return courseRepository.getCoursesByProfessor(id);
    }

    public Course getCourse(Long courseId) {
        return courseRepository.findById(courseId);
    }

    @Transactional
    public void updateOnlineLectureUrl(Long courseId, String url) {
        Course course = courseRepository.findById(courseId);

    }

    public List<Course> findCoursesByProfessorId(Long id) {
        return courseRepository.getCoursesByProfessor(id);
    }

    public List<Course> getEnrolledCoursesWithSchedule(Long studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findActiveByStudentId(studentId);
        return enrollments.stream()
                .map(Enrollment::getCourse)
                .collect(Collectors.toList());
    }

}
