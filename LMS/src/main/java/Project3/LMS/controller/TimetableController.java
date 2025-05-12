package Project3.LMS.controller;

import Project3.LMS.domain.Course;
import Project3.LMS.domain.Enrollment;
import Project3.LMS.domain.Student;
import Project3.LMS.domain.Timetable;
import Project3.LMS.repostiory.StudentRepository;
import Project3.LMS.repostiory.TimetableRepository;
import Project3.LMS.service.TimetableService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;
    private final StudentRepository studentRepository;

    /**
     *  timetable page get 요청
     */
    @GetMapping("/timetable")
    public String timetableView(Model model, @SessionAttribute("loginMember")  Student loginStudent){
        // 1. student를 DB에서 조회해옴
        Student student = loginStudent;

        // 2. 시간표 조회
        List<Timetable> timetableList = timetableService.getStudentTimetable(student);

        // 3. 요일+교시 기준으로 시간표 맵 구성 -> Thymeleaf에서 사용하기 편리하도록
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

        // 5. 화면에 데이터 전달
        model.addAttribute("courses", timetableService.getAllCourses());
        model.addAttribute("timetableMap", timetableMap);
        model.addAttribute("welcomeMessage", student.getName() + "님의 시간표");

        return "timetable/timetable.html";
    }

    /***
     * Timetable add Post 요청
     */
    @PostMapping("/timetable/add")
    public String addTimetable(@RequestParam(name = "day") String day,
                               @RequestParam(name = "time") int time,
                               @RequestParam(name = "courseId") Long courseId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Student loginStudent = (Student) session.getAttribute("loginMember");
        if (loginStudent == null) {
            return "redirect:/login"; // 로그인 안 된 경우 처리
        }

        try{
            timetableService.addTimetable(loginStudent, day, time, courseId);
        }catch(IllegalStateException | IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/timetable";
    }

    /**
     * 시간표 삭제 기능
     */
    @PostMapping("/timetable/delete")
    public String deleteTimetable(@RequestParam(name = "day") String day,
                                  @RequestParam("time") int time,
                                  HttpSession session) {
        Student student = (Student) session.getAttribute("loginMember");
        if (student == null) return "redirect:/login";

        timetableService.deleteByStudentAndDayAndTime(student, day, time);
        return "redirect:/timetable";
    }

}
