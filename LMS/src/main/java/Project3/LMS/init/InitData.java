//package Project3.LMS.init;
//
//import Project3.LMS.domain.Course;
//import Project3.LMS.domain.Professor;
//import Project3.LMS.domain.Student;
//import Project3.LMS.repostiory.CourseRepository;
//import Project3.LMS.repostiory.ProfessorRepository;
//import jakarta.annotation.PostConstruct;
//import jakarta.persistence.EntityManager;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//@Component
//@RequiredArgsConstructor
//public class InitData {
//
//    private final InitService initService;
//
//    @PostConstruct
//    public void init() {
//        initService.insertDummyData();
//    }
//
//    @Component
//    @RequiredArgsConstructor
//    @Transactional
//    static class InitService {
//
//        private final EntityManager em;
//
//        public void insertDummyData() {
//
//            // 학생
//            Student student = new Student();
//            student.setDepartment("컴퓨터공학");
//            student.setEmail("rkddk7165@naver.com");
//
//            // 교수 3명
//            Professor prof1 = new Professor();
//            prof1.setName("강현민");
//            em.persist(prof1);
//
//            Professor prof2 = new Professor();
//            prof2.setName("김현민");
//            em.persist(prof2);
//
//            Professor prof3 = new Professor();
//            prof3.setName("최현민");
//            em.persist(prof3);
//
//            // 강의 5개
//            Course course1 = new Course();
//            course1.setCourseName("자료구조");
//            course1.setCredits(3);
//            course1.setProfessor(prof1);
//            em.persist(course1);
//
//            Course course2 = new Course();
//            course2.setCourseName("운영체제");
//            course2.setCredits(3);
//            course2.setProfessor(prof2);
//            em.persist(course2);
//
//            Course course3 = new Course();
//            course3.setCourseName("컴퓨터구조");
//            course3.setCredits(2);
//            course3.setProfessor(prof2);
//            em.persist(course3);
//
//            Course course4 = new Course();
//            course4.setCourseName("알고리즘");
//            course4.setCredits(3);
//            course4.setProfessor(prof3);
//            em.persist(course4);
//
//            Course course5 = new Course();
//            course5.setCourseName("데이터베이스");
//            course5.setCredits(3);
//            course5.setProfessor(prof1);
//            em.persist(course5);
//        }
//    }
//}