package Project3.LMS.repostiory;

import Project3.LMS.domain.Assignment;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AssignmentRepository {

    private final EntityManager em;

    public void save(Assignment assignment) {
        em.persist(assignment);
    }

    public Assignment findById(Long id) {
        return em.find(Assignment.class, id);
    }

    // 과목별 과제 목록 조회
    public List<Assignment> findByCourseId(Long courseId) {
        return em.createQuery(
                        "SELECT a FROM Assignment a WHERE a.course.id = :courseId", Assignment.class)
                .setParameter("courseId", courseId)
                .getResultList();
    }

    // 과제 삭제
    public void delete(Assignment assignment) {
        em.remove(assignment);
    }
}