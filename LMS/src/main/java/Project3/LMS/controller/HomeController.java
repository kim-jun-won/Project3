package Project3.LMS.controller;

import Project3.LMS.domain.*;
import Project3.LMS.repostiory.EnrollmentRepository;
import Project3.LMS.service.CourseService;
import Project3.LMS.service.NoticeService;
import Project3.LMS.service.TimetableService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 메인 화면에는 시간표, 공지사항 등 많은 정보들이 필요하기에
 * 홈 컨트롤러 생성
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final TimetableService timetableService;
    private final NoticeService noticeService;
    private final CourseService courseService;
    private final EnrollmentRepository enrollmentRepository;

    /**
     * 로그인 후 화면
     */
    @GetMapping("/home")
    public String showHomePage(Model model, HttpSession session)  {//매개변수 추가가능
        //학생인지 교수인지 관리자인지 모르기때문에 Object로 가져옴
        Object loginMember = session.getAttribute("loginMember");

        //로그인된 세션이 학생
        if(loginMember instanceof Student) {
            Student student = (Student) loginMember;
            model.addAttribute("welcomeMessage", student.getName() + "님 환영합니다!");
            model.addAttribute("student",student);

            session.setAttribute("studentId", student.getId()); // 수강신청을 위해서 추가

            /**
             * enrollment service를 호출.
             * 세션에 있는 학생을 가지고 있는 course 객체 모두 출력
             */
            // 수강신청을 기반으로 한 실제 시간표 구성
            List<Course> enrolledCourses = courseService.getEnrolledCoursesWithSchedule(student.getId());
            Map<String, String[]> timetableMap = new HashMap<>();

            for (String day : List.of("월", "화", "수", "목", "금")) {
                timetableMap.put(day, new String[7]);  // 1~6교시 (0은 안 씀)
            }

            for (Course course : enrolledCourses) {
                // 가정: Course 클래스에 getDay()와 getTime()이 존재하거나, Schedule 정보를 가지고 있어야 함
                // 예시: Course가 월요일 3교시에 있다면
                String day = course.getDay();   // Course에 day 필드가 있을 경우
                int time = course.getTime();    // Course에 time 필드가 있을 경우

                String courseName = course.getCourseName();
                String professorName = course.getProfessor().getName();

                String[] times = timetableMap.get(day);
                if (times != null && time >= 1 && time <= 6) {
                    times[time] = courseName + "<br/><span style='font-size: 12px;'>" + professorName + "</span>";
                }
            }

            model.addAttribute("timetableMap", timetableMap);
            model.addAttribute("userRole","student");
            model.addAttribute("courses", enrolledCourses);

        }

        //로그인된 세션이 교수
        else if(loginMember instanceof Professor) {
            Professor professor = (Professor) loginMember;
            model.addAttribute("welcomeMessage", professor.getName() + "님 환영합니다!");
            model.addAttribute("professor",professor);
            model.addAttribute("professorId", professor.getId());

            // 교수인 경우 시간표 조회
            List<Timetable> timetableList = timetableService.getProfessorTimetable(professor.getId());

            Map<String, String[]> timetableMap = new HashMap<>();
            for (String day : List.of("월", "화", "수", "목", "금")) {
                timetableMap.put(day, new String[7]);
            }

            for (Timetable t : timetableList) {
                String[] times = timetableMap.get(t.getDay());
                if (times != null && t.getTime() >= 1 && t.getTime() <= 6) {
                    times[t.getTime()] = t.getCourse().getCourseName();
                }
            }

            model.addAttribute("timetableMap", timetableMap);
            model.addAttribute("userRole","professor");

            // 교수 과목 리스트 추가
            List<Course> courses = courseService.findCoursesByProfessorId(professor.getId());
            model.addAttribute("courses", courses);

        }

        //관리자일 경우
        else{
            model.addAttribute("welcomeMessage", "관리자님 환영합니다!");
            model.addAttribute("userRole","admin");
        }

        /**
         * 관리자 공지사항 목록전송
         * */
        List<Notice> adminNotices = noticeService.findAdminNotices(); // 관리자 작성 공지
        model.addAttribute("adminNotices", adminNotices);

        return "home";
    }

    /**
     * 학생이 특정과목 공지사항 클릭했을 때 공지사항 내용 보여주는 부분
     * */
    @GetMapping("/notice/student/course")
    public String studentCourseNotice(@RequestParam(name = "courseId") Long courseId, HttpSession session, Model model) {
        Object loginMember = session.getAttribute("loginMember");
        if (!(loginMember instanceof Student)) {
            return "redirect:/login"; // or 에러 페이지
        }
        Student student = (Student) loginMember;

        if (student == null) return "redirect:/login";

        Course course = courseService.findbyCourseId(courseId);

        // 수강 중인지 확인 (보안용)
        boolean enrolled = enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), courseId);
        if (!enrolled) return "redirect:/notice/student/list";

        List<Notice> notices = noticeService.findNoticesByCourse(course);
        model.addAttribute("notices", notices);
        model.addAttribute("course", course);

        return "notice/studentCourseNoticeList";
    }

    /**
     * 교수가 특정 강의의 공지사항 버튼 클릭 시 이동
     */
    @GetMapping("/notice/professor/course")
    public String professorCourseNotice(@RequestParam(name = "courseId") Long courseId,
                                        HttpSession session, Model model) {
        Professor professor = (Professor) session.getAttribute("loginMember");
        if (professor == null) return "redirect:/login";

        Course course = courseService.findbyCourseId(courseId);

        // 보안 검증: 이 과목의 담당 교수가 맞는지 확인
        if (!course.getProfessor().getId().equals(professor.getId())) {
            return "redirect:/notice/professor/list";
        }

        List<Notice> notices = noticeService.findNoticesByCourse(course);
        model.addAttribute("notices", notices);
        model.addAttribute("course", course);

        return "notice/professorCourseNoticeList";
    }

}
