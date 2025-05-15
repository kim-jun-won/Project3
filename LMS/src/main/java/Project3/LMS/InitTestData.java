package Project3.LMS;

import Project3.LMS.domain.*;
import Project3.LMS.repostiory.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

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

    @Bean
    public CommandLineRunner initTestDataRunner() {
        return new InitDataRunner(
                userRepository, studentRepo, professorRepo, timetableRepository, enrollmentRepository, noticeRepository, em
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
            if (userRepository.existsByEmail("professor1@kw.ac.kr")) return;

            // 1. 교수 5명 등록 (이름 현실감 있게)
            String[][] professorInfos = {
                    {"이기훈", "professor1@kw.ac.kr", "1234"},
                    {"장민호", "professor2@kw.ac.kr", "12345"},
                    {"김다영", "professor3@kw.ac.kr", "123456"},
                    {"박수현", "professor4@kw.ac.kr", "1234567"},
                    {"최은지", "professor5@kw.ac.kr", "12345678"}
            };

            List<Professor> professorList = new ArrayList<>();
            for (String[] info : professorInfos) {
                User user = new User();
                user.setName(info[0]);
                user.setUid(info[2]);
                user.setPassword("1234");
                user.setEmail(info[1]);
                user.setUserType(UserType.PROFESSOR);
                user.setDepartment("컴퓨터정보공학부");
                user.setPhoneNumber("010-9" + info[2].substring(1)); // 예: 010-9000-0001
                userRepository.save(user);

                Professor professor = new Professor();
                professor.setName(user.getName());
                professor.setPid(user.getUid());
                professor.setPassword(user.getPassword());
                professor.setEmail(user.getEmail());
                professor.setDepartment(user.getDepartment());
                professor.setPhoneNumber(user.getPhoneNumber());
                professorRepo.save(professor);
                professorList.add(professor);
            }

            // 2. 과목 8개 생성 → 교수들에게 2개씩 공평하게 분배
            String[] courseNames = {
                    "자료구조", "운영체제", "웹프로그래밍", "컴퓨터네트워크",
                    "인공지능", "알고리즘", "데이터베이스", "객체지향프로그래밍"
            };
            List<Course> courseList = new ArrayList<>();
            for (int i = 0; i < courseNames.length; i++) {
                Course course = new Course();
                course.setCourseName(courseNames[i]);
                course.setCredits(3);
                course.setProfessor(professorList.get(i / 2)); // 교수 5명 → 2개씩 분배
                em.persist(course);
                courseList.add(course);

                Syllabus syllabus = new Syllabus();
                syllabus.setCourse(course);
                syllabus.setContent(courseNames[i] + " 과목은 기본 개념과 실습을 포함합니다.");
                em.persist(syllabus);
            }

            // 3. 시간표 분산용 데이터
            String[][] timeTableSlots = {
                    {"월", "1"}, {"화", "2"}, {"수", "3"}, {"목", "4"},
                    {"금", "5"}, {"월", "2"}, {"수", "1"}, {"화", "4"}
            };

            // 4. 학생 생성 및 수강신청
            String[][] studentInfos = {
                    {"김지훈", "2020202001", "kimjh01@kw.ac.kr", "010-1111-2222"},
                    {"박소연", "2020202002", "parksy02@kw.ac.kr", "010-2222-3333"},
                    {"이준석", "2020202003", "leejs03@kw.ac.kr", "010-3333-4444"},
                    {"최민정", "2020202004", "choimj04@kw.ac.kr", "010-4444-5555"},
                    {"정재현", "2020202005", "jungjh05@kw.ac.kr", "010-5555-6666"},
                    {"김준원", "2020202095", "junwon@kw.ac.kr", "010-6666-7777"},
                    {"홍왕기", "2020202060", "leejd8130@kw.ac.kr", "010-5346-8130"},
                    {"강현민", "2020202092", "abc8130@kw.ac.kr", "010-1234-8130"},
                    {"박세영", "2020202093", "123a0@kw.ac.kr", "010-2345-8130"},
            };

            for (String[] info : studentInfos) {
                User user = new User();
                user.setName(info[0]);
                user.setUid(info[1]);
                user.setPassword("1234");
                user.setEmail(info[2]);
                user.setUserType(UserType.STUDENT);
                user.setDepartment("컴퓨터정보공학부");
                user.setPhoneNumber(info[3]);
                userRepository.save(user);

                Student student = new Student();
                student.setName(info[0]);
                student.setSid(info[1]);
                student.setPassword("1234");
                student.setEmail(info[2]);
                student.setDepartment("컴퓨터정보공학부");
                student.setPhoneNumber(info[3]);
                studentRepo.save(student);

                // 5. 수강신청 8과목 전체 등록
                for (int i = 0; i < courseList.size(); i++) {
                    Course course = courseList.get(i);
                    String[] slot = timeTableSlots[i]; // 요일-시간 짝
                    enroll(student, course, slot[0], Integer.parseInt(slot[1]));
                }
            }

            // 6. 교수 공지사항 예시
            Notice notice = new Notice();
            notice.setDate(LocalDateTime.now());
            notice.setWriterType(NoticeWriterType.PROFESSOR);
            notice.setProfessor(professorList.get(0)); // 이기훈
            notice.setCourse(courseList.get(0)); // 자료구조
            notice.setTitle("[자료구조] 1주차 예습 및 과제 안내");
            notice.setContent("자료구조 1주차 수업은 배열과 연결 리스트 개념입니다.\n예습하고, 과제는 LMS에 업로드하세요.");
            noticeRepository.save(notice);
        }

        private Course createCourse(String name, Professor professor) {
            Course course = new Course();
            course.setCourseName(name);
            course.setProfessor(professor);
            course.setCredits(3);
            em.persist(course);
            return course;
        }

        private void enroll(Student student, Course course, String day, int time) {
            Enrollment e = new Enrollment();
            e.setStudent(student);
            e.setCourse(course);
            enrollmentRepository.save(e);

            Timetable t = new Timetable();
            t.setStudent(student);
            t.setCourse(course);
            t.setDay(day);
            t.setTime(time);
            timetableRepository.save(t);
        }
    }
}
