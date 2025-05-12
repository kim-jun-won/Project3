package Project3.LMS;

import Project3.LMS.domain.*;
import Project3.LMS.repostiory.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Subgraph;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class InitTestData {

    private final UserRepository userRepository;
    private final StudentRepository studentRepo;
    private final ProfessorRepository professorRepo;
    private final TimetableRepository timetableRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final NoticeRepository noticeRepository;
    private final EntityManager em;

    // Bean + CommandRunner -> 시작 시 한번 실행하는 메서드
    @Bean
    public CommandLineRunner initTestDataRunner() {
        return new InitDataRunner(
                userRepository, studentRepo, professorRepo, timetableRepository, enrollmentRepository, noticeRepository,em
        );
    }

    @RequiredArgsConstructor
    static class InitDataRunner implements CommandLineRunner {

        private final UserRepository userRepository;
        private final StudentRepository studentRepo;
        private final ProfessorRepository professorRepo;
        private final TimetableRepository timetableRepository;
        private final EnrollmentRepository enrollmentRepository;
        private final NoticeRepository noticeRepository;
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

            Course course3 = new Course();
            course3.setCourseName("운영체제");
            course3.setProfessor(professor);
            em.persist(course3);

            /** 4. 강의계획서 등록 */
            Syllabus syllabus = new Syllabus();
            syllabus.setCourse(course);
            syllabus.setContent("이 과목은 자료구조와 알고리즘의 기본 개념을 학습합니다.");
            em.persist(syllabus);

            Syllabus syllabus1 = new Syllabus();
            syllabus1.setCourse(course1);
            syllabus1.setContent("이 과목은 선형대수학을 학습합니다.");
            em.persist(syllabus1);

            Syllabus syllabus2 = new Syllabus();
            syllabus2.setCourse(course2);
            syllabus2.setContent("이 과목을 소프트웨어 방법론을 학습합니다.");
            em.persist(syllabus2);

            /** 4. 타임테이블 등록 */

            // 자료구조
            Timetable tt = new Timetable();
            tt.setStudent(student);
            tt.setCourse(course);
            tt.setDay("화");
            tt.setTime(3);
            timetableRepository.save(tt);

            // 수강과목: 선형대수학
            Timetable tt1 = new Timetable();
            tt1.setStudent(student);
            tt1.setCourse(course1);
            tt1.setDay("수");
            tt1.setTime(2);
            timetableRepository.save(tt1);

            // 수강과목: 소프트웨어공학
            Timetable tt2 = new Timetable();
            tt2.setStudent(student);
            tt2.setCourse(course2);
            tt2.setDay("목");
            tt2.setTime(1);
            timetableRepository.save(tt2);

            // 수강과목: 운영체제 (강의계획서 없음)
            Timetable tt3 = new Timetable();
            tt3.setStudent(student);
            tt3.setCourse(course3);
            tt3.setDay("금");
            tt3.setTime(4);
            timetableRepository.save(tt3);

            /** 수강신청과목 저장*/
            Enrollment enroll1 = new Enrollment();
            enroll1.setStudent(student);
            enroll1.setCourse(course); // 자료구조
            enrollmentRepository.save(enroll1);

            Enrollment enroll2 = new Enrollment();
            enroll2.setStudent(student);
            enroll2.setCourse(course1); // 선형대수학
            enrollmentRepository.save(enroll2);

            Enrollment enroll3 = new Enrollment();
            enroll3.setStudent(student);
            enroll3.setCourse(course2); // 소프트웨어공학
            enrollmentRepository.save(enroll3);

            Enrollment enroll4 = new Enrollment();
            enroll4.setStudent(student);
            enroll4.setCourse(course3); // 소프트웨어공학
            enrollmentRepository.save(enroll4);


            /**
             * 공지사항 추가
             */
            Notice notice = new Notice();
            notice.setDate(LocalDateTime.now());
            notice.setWriterType(NoticeWriterType.PROFESSOR);
            notice.setProfessor(professor);
            notice.setCourse(course1);
            notice.setTitle("휴강안내");
            notice.setContent("교수 개인 사정으로 휴강합니다.");
            noticeRepository.save(notice);
        }
    }
}
