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
        Object loginMember = session.getAttribute("loginMember");;

        //로그인된 세션이 학생
        if(loginMember instanceof Student) {
            Student student = (Student) loginMember;
            model.addAttribute("welcomeMessage", student.getName() + "님 환영합니다!");
            model.addAttribute("student",student);

            session.setAttribute("studentId", student.getId()); // 수강신청을 위해서 추가

            /**
             * timetable service를 호출.
             * 세션에 있는 학생을 가지고 있는 timetable 객체 모두 출력
             */
            // 시간표를 요일+교시별 맵으로 재구성
            List<Timetable> timetableList = timetableService.getStudentTimetable(student);
            Map<String, String[]> timetableMap= new HashMap<>();
            for (String day : List.of("월", "화", "수", "목", "금")) {
                timetableMap.put(day, new String[7]);  // 1~6교시 (0은 안 씀)
            }

            for(Timetable t : timetableList){
                String[] times = timetableMap.get(t.getDay());
                if(times!=null && t.getTime()>=1 && t.getTime()<=6){
                    times[t.getTime()] = t.getCourse().getCourseName();
                }
            }

            model.addAttribute("timetableMap", timetableMap);


            /**
             * 수강과목 리스트 전달
             * */
            List<Course> courses = timetableList.stream()
                    .map(Timetable::getCourse)
                    .distinct()
                    .toList();
            model.addAttribute("courses", courses);
        }

        //로그인된 세션이 교수
        else if(loginMember instanceof Professor) {
            Professor professor = (Professor) loginMember;
            model.addAttribute("welcomeMessage", professor.getName() + "님 환영합니다!");
            model.addAttribute("professor",professor);
            model.addAttribute("professorId", professor.getId());


        }

        //관리자일 경우
        else{
            model.addAttribute("welcomeMessage", "관리자님 환영합니다!");
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
    public String studentCourseNotice(@RequestParam Long courseId, HttpSession session, Model model) {
        Student student = (Student) session.getAttribute("loginMember");
        if (student == null) return "redirect:/login";

        Course course = courseService.getCourse(courseId);

        // 수강 중인지 확인 (보안용)
        boolean enrolled = enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), courseId);
        if (!enrolled) return "redirect:/notice/student/list";

        List<Notice> notices = noticeService.findNoticesByCourse(course);
        model.addAttribute("notices", notices);
        model.addAttribute("course", course);

        return "notice/studentCourseNoticeList";
    }



}
