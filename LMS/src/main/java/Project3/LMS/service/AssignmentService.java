package Project3.LMS.service;

import Project3.LMS.domain.Assignment;
import Project3.LMS.domain.Course;
import Project3.LMS.domain.Professor;
import Project3.LMS.repostiory.AssignmentRepository;
import Project3.LMS.repostiory.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final ProfessorService professorService;
    private final CourseRepository courseRepository;

    public Assignment createAssignment(String professorPid, Long courseId, String title, String content, LocalDateTime dueDate) {
        Professor professor = professorService.findByPid(professorPid);  // 변경됨
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Assignment assignment = new Assignment();
        assignment.setId(System.currentTimeMillis());  // ID 생성 전략은 추후 조정
        assignment.setProfessor(professor);
        assignment.setCourse(course);
        assignment.setTitle(title);
        assignment.setContent(content);
        assignment.setDue_date(dueDate);
        assignment.setCreated_time(LocalDateTime.now());
        assignment.setUpdated_time(LocalDateTime.now());

        professor.getAssignments().add(assignment);
        course.getAssignments().add(assignment);

        return assignmentRepository.save(assignment);
    }
}