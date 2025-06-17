package Project3.LMS.service;

import Project3.LMS.domain.Assignment;
import Project3.LMS.domain.AssignmentFile;
import Project3.LMS.domain.Course;
import Project3.LMS.domain.Professor;
import Project3.LMS.repostiory.AssignmentFileRepository;
import Project3.LMS.repostiory.AssignmentRepository;
import Project3.LMS.repostiory.CourseRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentFileRepository assignmentFileRepository;
    private final CourseRepository courseRepository;
    private final ProfessorService professorService;
    private final EntityManager em;

    @Transactional
    public Assignment createAssignment(String professorPid, Long courseId, String title, String content, LocalDateTime dueDate) {
        Course course = courseRepository.findById(courseId);
        Professor professor = professorService.findByPid(professorPid);

        Assignment assignment = new Assignment();
        assignment.setId(System.currentTimeMillis());
        assignment.setProfessor(professor);
        assignment.setCourse(course);
        assignment.setTitle(title);
        assignment.setContent(content);
        assignment.setDue_date(dueDate);
        assignment.setCreated_time(LocalDateTime.now());
        assignment.setUpdated_time(LocalDateTime.now());

        professor.getAssignments().add(assignment);
        course.getAssignments().add(assignment);

        assignmentRepository.save(assignment);
        return assignment;
    }

    @Transactional
    public Assignment createAssignmentWithFile(String professorPid, Long courseId, String title, String content,
                                               LocalDateTime dueDate, String fileName, String filePath, String fileType) {
        Assignment assignment = createAssignment(professorPid, courseId, title, content, dueDate);

        AssignmentFile file = new AssignmentFile();
        file.setAssignment(assignment);
        file.setFile_name(fileName);
        file.setFile_path(filePath);
        file.setFile_type(fileType);
        file.setUpdated_time(LocalDateTime.now());

        assignmentFileRepository.save(file);         // 파일 저장
        assignment.getFiles().add(file);             // 연관관계 유지

        return assignment;
    }

    @Transactional
    public void deleteAssignment(Long assignmentId) {
        Optional<Assignment> optional = Optional.ofNullable(em.find(Assignment.class, assignmentId));
        Assignment assignment = optional.orElseThrow(() -> new IllegalArgumentException("과제를 찾을 수 없습니다."));
        assignmentRepository.delete(assignment);
    }

    @Transactional(readOnly = true)
    public Assignment findById(Long assignmentId) {
        return assignmentRepository.findById(assignmentId);
    }

}
