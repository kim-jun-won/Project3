package Project3.LMS;

import Project3.LMS.domain.*;
import Project3.LMS.repostiory.ProfessorRepository;
import Project3.LMS.repostiory.StudentRepository;
import Project3.LMS.repostiory.TimetableRepository;
import Project3.LMS.repostiory.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Subgraph;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class InitTestData {

    private final UserRepository userRepository;
    private final StudentRepository studentRepo;
    private final ProfessorRepository professorRepo;
    private final TimetableRepository timetableRepository;
    private final EntityManager em;

    @Bean
    public CommandLineRunner initTestDataRunner() {
        return new InitDataRunner(
                userRepository, studentRepo, professorRepo, timetableRepository, em
        );
    }

    @RequiredArgsConstructor
    static class InitDataRunner implements CommandLineRunner {

        private final UserRepository userRepository;
        private final StudentRepository studentRepo;
        private final ProfessorRepository professorRepo;
        private final TimetableRepository timetableRepository;
        private final EntityManager em;

        @Override
        @Transactional
        public void run(String... args) {
            if (userRepository.existsByEmail("junwon2631@kw.ac.kr")) return;

            /** 1. 학생 계정 및 엔티티 생성 */
            User user = new User();
            user.setName("김준원");
            user.setUid("2020202095");
            user.setPassword("1234");
            user.setEmail("junwon2631@kw.ac.kr");
            user.setUserType(UserType.STUDENT);
            user.setDepartment("컴퓨터정보공학부");
            user.setPhoneNumber("010-2425-4974");
            userRepository.save(user);


            Student student = new Student();
            student.setName(user.getName());
            student.setSid(user.getUid());
            student.setPassword(user.getPassword());
            student.setEmail(user.getEmail());
            student.setDepartment(user.getDepartment());
            student.setPhoneNumber(user.getPhoneNumber())   ;
            studentRepo.save(student);

            /** 2. 교수 계정 및 엔티티 생성 */
            User professorUser = new User();
            professorUser.setName("이기훈");
            professorUser.setUid("9999999999");
            professorUser.setPassword("1234");
            professorUser.setEmail("professor@kw.ac.kr");
            professorUser.setUserType(UserType.PROFESSOR);
            professorUser.setDepartment("컴퓨터정보공학부");
            professorUser.setPhoneNumber("010-1111-1111");
            userRepository.save(professorUser);

            Professor professor = new Professor();
            professor.setName(professorUser.getName());
            professor.setPid(professorUser.getUid());
            professor.setPassword(professorUser.getPassword());
            professor.setEmail(professorUser.getEmail());
            professor.setDepartment(professorUser.getDepartment());
            professor.setPhoneNumber(professorUser.getPhoneNumber());
            professorRepo.save(professor);

            /** 3. 과목 등록 */
            Course course = new Course();
            course.setCourseName("자료구조");
            course.setProfessor(professor);
            em.persist(course); // 트랜잭션 안에서 안전하게 실행됨

            Course course1 = new Course();
            course1.setCourseName("선형대수학");
            course1.setProfessor(professor);
            em.persist(course1);

            Course course2 = new Course();
            course2.setCourseName("소프트웨어공학");
            course2.setProfessor(professor);
            em.persist(course2);

            /** 4. 강의계획서 등록 */
            Syllabus syllabus = new Syllabus();
            syllabus.setId(course.getId()); // Course와 1:1 매핑 시 ID 같게 설정
            syllabus.setCourse(course);
            syllabus.setContent("이 과목은 자료구조와 알고리즘의 기본 개념을 학습합니다.");
            em.persist(syllabus);

            Syllabus syllabus1 = new Syllabus();
            syllabus1.setId(course1.getId());
            syllabus1.setCourse(course1);
            syllabus1.setContent("이 과목은 선형대수학을 학습합니다.");
            em.persist(syllabus1);

            Syllabus syllabus2 = new Syllabus();
            syllabus2.setId(course2.getId());
            syllabus2.setCourse(course2);
            syllabus2.setContent("이 과목을 소프트웨어 방법론을 학습합니다.");
            em.persist(syllabus2);

            Timetable tt = new Timetable();
            tt.setStudent(student);
            tt.setCourse(course);
            tt.setDay("화");
            tt.setTime(3);
            timetableRepository.save(tt);
        }
    }
}
