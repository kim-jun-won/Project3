package Project3.LMS.controller;

import Project3.LMS.domain.Admin;
import Project3.LMS.domain.Professor;
import Project3.LMS.domain.Student;
import Project3.LMS.domain.Timetable;
import Project3.LMS.service.TimetableService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
            model.addAttribute("studentId", student.getId());

            /**
             * timetable service를 호출.
             * 세션에 있는 학생을 가지고 있는 timetable 객체 모두 출력
             */
            // 시간표를 요일+교시별 맵으로 재구성
            List<Timetable> timetables = timetableService.getStudentTimetable(student);

            Map<String, Map<Integer, String>> timetableMap = new HashMap<>();
            for (Timetable tt : timetables) {
                timetableMap
                        .computeIfAbsent(tt.getDay(), d -> new HashMap<>())
                        .put(tt.getTime(), tt.getCourse().getCourseName());
            }

            model.addAttribute("timetableMap", timetableMap);
        }

        //로그인된 세션이 교수
        else if(loginMember instanceof Professor) {
            Professor professor = (Professor) loginMember;
            model.addAttribute("welcomeMessage", professor.getName() + "님 환영합니다!");
            model.addAttribute("professorId", professor.getId());
        }

        //관리자일 경우
        else{
            model.addAttribute("welcomeMessage", "관리자님 환영합니다!");
        }

        return "home";
    }
}
