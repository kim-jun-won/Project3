package Project3.LMS.repostiory;

import Project3.LMS.domain.Enrollment;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EnrollmentRepository {

    private final EntityManager em;

    public void save(Enrollment enrollment) {
        em.persist(enrollment);
    }

    public void delete(Enrollment enrollment) {
        em.remove(enrollment);
    }

    public Enrollment findOne(Long id) {
        return em.find(Enrollment.class, id);
    }

    public List<Enrollment> findAll() {
        return em.createQuery("select e from Enrollment e", Enrollment.class)
                .getResultList();
    }

    // 학생 ID로 수강 신청 목록 조회
    public List<Enrollment> findByStudentId(Long studentId) {
        return em.createQuery(
                        "SELECT e FROM Enrollment e WHERE e.student.id = :studentId", Enrollment.class)
                .setParameter("studentId", studentId)
                .getResultList();
    }

    public List<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId) {
        return em.createQuery(
                        "SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.course.id = :courseId", Enrollment.class)
                .setParameter("studentId", studentId)
                .setParameter("courseId", courseId)
                .getResultList();
    }



}
