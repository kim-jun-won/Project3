package Project3.LMS.service;

import Project3.LMS.domain.*;
import Project3.LMS.repostiory.AssignmentRepository;
import Project3.LMS.repostiory.AssignmentSubmissionRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AssignmentSubmissionServiceTest {

    class DummyAssignmentRepository extends AssignmentRepository {
        private final Assignment assignment;

        public DummyAssignmentRepository(Assignment assignment) {
            super(null); // EntityManager 안 씀
            this.assignment = assignment;
        }

        @Override
        public Assignment findById(Long id) {
            return assignment;
        }
    }

    class DummyStudentService extends StudentService {
        private final Student student;

        public DummyStudentService(Student student) {
            super(null);
            this.student = student;
        }

        @Override
        public Student findBySid(String sid) {
            return student;
        }
    }

    class DummySubmissionRepository extends AssignmentSubmissionRepository {
        public final List<AssignmentSubmission> savedList = new ArrayList<>();
        public boolean exists = false;

        public DummySubmissionRepository() {
            super(null);
        }

        @Override
        public void save(AssignmentSubmission submission) {
            savedList.add(submission);
        }

        @Override
        public AssignmentSubmission findById(Long id) {
            return savedList.stream().filter(s -> s.getId() == id).findFirst().orElse(null);
        }

        @Override
        public List<AssignmentSubmission> findByAssignmentId(Long assignmentId) {
            List<AssignmentSubmission> result = new ArrayList<>();
            for (AssignmentSubmission s : savedList) {
                if (s.getAssignment().getId() == (assignmentId)) {
                    result.add(s);
                }
            }
            return result;
        }

        @Override
        public boolean existsByStudentAndAssignment(Student student, Assignment assignment) {
            return exists;
        }

        @Override
        public AssignmentSubmission findByStudentAndAssignment(Student student, Assignment assignment) {
            return savedList.stream()
                    .filter(s -> s.getStudent() == student && s.getAssignment() == assignment)
                    .findFirst()
                    .orElse(null);
        }
    }

    @Test
    void 과제제출_성공() {
        // given
        Student student = new Student();
        student.setSid("s001");
        student.setAssignmentSubmissions(new ArrayList<>());

        Assignment assignment = new Assignment();
        assignment.setId(123L);

        DummySubmissionRepository repo = new DummySubmissionRepository();
        AssignmentSubmissionService service = new AssignmentSubmissionService(
                repo, new DummyAssignmentRepository(assignment), new DummyStudentService(student)
        );

        // when
        AssignmentSubmission submission = service.submitAssignment(
                123L, "s001", "제목", "내용", "경로", "파일.pdf", "pdf"
        );

        // then
        assertThat(submission.getTitle()).isEqualTo("제목");
        assertThat(submission.getFile_name()).isEqualTo("파일.pdf");
        assertThat(repo.savedList).contains(submission);
        assertThat(student.getAssignmentSubmissions()).contains(submission);
    }

    @Test
    void 제출여부확인_true() {
        // given
        Student student = new Student();
        Assignment assignment = new Assignment();

        DummySubmissionRepository repo = new DummySubmissionRepository();
        repo.exists = true;

        AssignmentSubmissionService service = new AssignmentSubmissionService(
                repo, null, null
        );

        // when
        boolean result = service.isSubmitted(student, assignment);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 제출목록조회() {
        // given
        Assignment assignment = new Assignment();
        assignment.setId(999L);

        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setAssignment(assignment);

        DummySubmissionRepository repo = new DummySubmissionRepository();
        repo.savedList.add(submission);

        AssignmentSubmissionService service = new AssignmentSubmissionService(repo, null, null);

        // when
        List<AssignmentSubmission> result = service.findByAssignment(999L);

        // then
        assertThat(result).containsExactly(submission);
    }

    @Test
    void 제출단건조회() {
        // given
        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setId(1234L);

        DummySubmissionRepository repo = new DummySubmissionRepository();
        repo.savedList.add(submission);

        AssignmentSubmissionService service = new AssignmentSubmissionService(repo, null, null);

        // when
        AssignmentSubmission found = service.findById(1234L);

        // then
        assertThat(found).isEqualTo(submission);
    }

    @Test
    void 채점_성공() {
        // given
        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setId(7L);

        DummySubmissionRepository repo = new DummySubmissionRepository();
        repo.savedList.add(submission);

        AssignmentSubmissionService service = new AssignmentSubmissionService(repo, null, null);

        // when
        service.gradeSubmission(7L, 95, "잘했음");

        // then
        assertThat(submission.getGrade()).isEqualTo(95);
        assertThat(submission.getFeedback()).isEqualTo("잘했음");
    }
}
