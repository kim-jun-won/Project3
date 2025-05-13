package Project3.LMS;

import Project3.LMS.domain.Course;
import Project3.LMS.domain.Professor;
import Project3.LMS.domain.Student;
import Project3.LMS.domain.Timetable;
import Project3.LMS.repostiory.StudentRepository;
import Project3.LMS.repostiory.TimetableRepository;
import Project3.LMS.service.TimetableService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class TimetableTest {
    @Autowired private TimetableService timetableService;
    @Autowired private TimetableRepository timetableRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired
    EntityManager em;

    @Test
    public void 시간표_저장(){
        Student student = new Student();
        student.setName("김준원");
        studentRepository.save(student);

        Professor professor = new Professor();
        professor.setName("이교수");
        em.persist(professor);

        Course course = new Course();
        course.setCourseName("소프트웨어공학");
        course.setProfessor(professor);
        em.persist(course);

        Timetable timetable = new Timetable();
        timetable.setStudent(student);
        timetable.setCourse(course);
        timetable.setDay("월");
        timetable.setTime(2);
        timetableRepository.save(timetable);

        // when
        List<Timetable> result = timetableService.getStudentTimetable(student);

        // then
        assertThat(result).hasSize(1);
        Timetable retrieved = result.get(0);
        assertThat(retrieved.getCourse().getCourseName()).isEqualTo("소프트웨어공학");
        assertThat(retrieved.getDay()).isEqualTo("월");
        assertThat(retrieved.getTime()).isEqualTo(2);
    }
}
