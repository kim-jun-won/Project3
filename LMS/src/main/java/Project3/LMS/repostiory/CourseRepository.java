package Project3.LMS.repostiory;

import Project3.LMS.domain.Course;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CourseRepository {

    private final EntityManager em;

    public void save(Course course) {
        em.persist(course);
    }

    public void delete(Course course) {
        em.remove(course);
    }

    public Course findOne(Long id) {
        return em.find(Course.class, id);
    }


    public List<Course> findAll(CourseSearch courseSearch) {
        String jpql = "SELECT c FROM Course c WHERE 1=1";

        if (courseSearch.getCourseName() != null && !courseSearch.getCourseName().isEmpty()) {
            jpql += " AND c.name LIKE :courseName";
        }

        if (courseSearch.getProfessorName() != null && !courseSearch.getProfessorName().isEmpty()) {
            jpql += " AND c.professor.name LIKE :professorName";
        }

        var query = em.createQuery(jpql, Course.class);

        if (courseSearch.getCourseName() != null && !courseSearch.getCourseName().isEmpty()) {
            query.setParameter("courseName", "%" + courseSearch.getCourseName() + "%");
        }

        if (courseSearch.getProfessorName() != null && !courseSearch.getProfessorName().isEmpty()) {
            query.setParameter("professorName", "%" + courseSearch.getProfessorName() + "%");
        }

        return query.getResultList();
    }


    public List<Course> findByName(String courseName) {
        return em.createQuery("select c from Course c where c.courseName = :courseName", Course.class)
                .setParameter("courseName", courseName)
                .getResultList();
    }
}
