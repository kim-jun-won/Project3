package Project3.LMS.service;

import Project3.LMS.domain.Assignment;
import Project3.LMS.domain.Course;
import Project3.LMS.domain.Professor;
import Project3.LMS.repostiory.AssignmentFileRepository;
import Project3.LMS.repostiory.AssignmentRepository;
import Project3.LMS.repostiory.CourseRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class AssignmentServiceTest {

    @Test
    void 과제출제_성공() {
        // given
        AssignmentRepository assignmentRepo = mock(AssignmentRepository.class);
        AssignmentFileRepository fileRepo = mock(AssignmentFileRepository.class);
        CourseRepository courseRepo = mock(CourseRepository.class);
        ProfessorService professorService = mock(ProfessorService.class);
        EntityManager em = mock(EntityManager.class);

        AssignmentService service = new AssignmentService(assignmentRepo, fileRepo, courseRepo, professorService, em);

        Course course = new Course();
        Professor professor = new Professor();

        when(courseRepo.findById(1L)).thenReturn(course);
        when(professorService.findByPid("p01")).thenReturn(professor);

        // when
        Assignment assignment = service.createAssignment("p01", 1L, "과제1", "설명입니다", LocalDateTime.now().plusDays(3));

        // then
        assertThat(assignment.getTitle()).isEqualTo("과제1");
        assertThat(assignment.getProfessor()).isEqualTo(professor);
        assertThat(assignment.getCourse()).isEqualTo(course);
        verify(assignmentRepo).save(any(Assignment.class));
    }

    @Test
    void 과제삭제_성공() {
        // given
        AssignmentRepository assignmentRepo = mock(AssignmentRepository.class);
        AssignmentFileRepository fileRepo = mock(AssignmentFileRepository.class);
        CourseRepository courseRepo = mock(CourseRepository.class);
        ProfessorService professorService = mock(ProfessorService.class);
        EntityManager em = mock(EntityManager.class);

        AssignmentService service = new AssignmentService(assignmentRepo, fileRepo, courseRepo, professorService, em);

        Assignment assignment = new Assignment();
        when(em.find(Assignment.class, 10L)).thenReturn(assignment);

        // when
        service.deleteAssignment(10L);

        // then
        verify(assignmentRepo).delete(assignment);
    }

    @Test
    void 과제삭제_예외_발생() {
        // given
        AssignmentRepository assignmentRepo = mock(AssignmentRepository.class);
        AssignmentFileRepository fileRepo = mock(AssignmentFileRepository.class);
        CourseRepository courseRepo = mock(CourseRepository.class);
        ProfessorService professorService = mock(ProfessorService.class);
        EntityManager em = mock(EntityManager.class);

        AssignmentService service = new AssignmentService(assignmentRepo, fileRepo, courseRepo, professorService, em);

        when(em.find(Assignment.class, 999L)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> service.deleteAssignment(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("과제를 찾을 수 없습니다.");
    }
}
