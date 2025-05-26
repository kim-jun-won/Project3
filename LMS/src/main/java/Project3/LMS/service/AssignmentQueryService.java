package Project3.LMS.service;

import Project3.LMS.domain.Assignment;
import Project3.LMS.domain.Course;
import Project3.LMS.domain.Professor;
import Project3.LMS.domain.Student;
import Project3.LMS.repostiory.AssignmentRepository;
import Project3.LMS.repostiory.CourseRepository;
import Project3.LMS.service.ProfessorService;
import Project3.LMS.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentQueryService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final StudentService studentService;
    private final ProfessorService professorService;

    // 학생이 수강 중인 강의의 과제 목록을 조회
    public List<Assignment> findAssignableAssignments(String studentSid) {
        Student student = studentService.findBySid(studentSid);
        return student.getEnrollments().stream()
                .map(enrollment -> enrollment.getCourse().getAssignments())
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    // 교수 PID로 본인이 맡은 강의 목록 조회
    public List<Course> getCoursesByProfessorPid(String professorPid) {
        Professor professor = professorService.findByPid(professorPid);
        return courseRepository.findByProfessor(professor);
    }

    public List<Assignment> findByCourseId(Long courseId) {
        return assignmentRepository.findByCourseId(courseId);
    }

    public Course findCourse(Long courseId) {
        return courseRepository.findById(courseId);
    }

    public Assignment findById(Long assignmentId) {
        return Optional.ofNullable(assignmentRepository.findById(assignmentId))
                .orElseThrow(() -> new IllegalArgumentException("과제를 찾을 수 없습니다."));
    }

    public List<Course> getCoursesByStudentSid(String studentSid) {
        Student student = studentService.findBySid(studentSid);
        return student.getEnrollments().stream()
                .map(enrollment -> enrollment.getCourse())
                .distinct()
                .collect(Collectors.toList());
    }

}