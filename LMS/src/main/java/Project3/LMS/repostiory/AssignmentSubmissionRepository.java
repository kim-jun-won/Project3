package Project3.LMS.repostiory;

import Project3.LMS.domain.Assignment;
import Project3.LMS.domain.AssignmentSubmission;
import Project3.LMS.domain.Student;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AssignmentSubmissionRepository {

    private final EntityManager em;

    public void save(AssignmentSubmission submission) {
        em.persist(submission);
    }

    public AssignmentSubmission findById(Long id) {
        return em.find(AssignmentSubmission.class, id);
    }

    public List<AssignmentSubmission> findByAssignmentId (Long assignmentId){
        return em.createQuery("SELECT s FROM AssignmentSubmission s WHERE s.assignment.id = :assignmentId", AssignmentSubmission.class)
                .setParameter("assignmentId", assignmentId)
                .getResultList();
    }

    public boolean existsByStudentAndAssignment(Student student, Assignment assignment) {
        Long count = em.createQuery(
                        "SELECT COUNT(s) FROM AssignmentSubmission s WHERE s.student = :student AND s.assignment = :assignment",
                        Long.class
                )
                .setParameter("student", student)
                .setParameter("assignment", assignment)
                .getSingleResult();

        return count > 0;
    }

    public AssignmentSubmission findByStudentAndAssignment(Student student, Assignment assignment) {
        return em.createQuery(
                        "SELECT s FROM AssignmentSubmission s WHERE s.student = :student AND s.assignment = :assignment",
                        AssignmentSubmission.class
                )
                .setParameter("student", student)
                .setParameter("assignment", assignment)
                .getSingleResult(); // 또는 getResultStream().findFirst().orElse(null); 로 예외 방지 가능
    }

}
