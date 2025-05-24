package Project3.LMS.controller;

import Project3.LMS.domain.Course;
import Project3.LMS.domain.LearningTalk;
import Project3.LMS.domain.Professor;
import Project3.LMS.domain.Student;
import Project3.LMS.service.CourseService;
import Project3.LMS.service.LearningTalkService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/courses/{courseId}/learning-talk")
public class LearningTalkController {

    private final CourseService courseService;
    private final LearningTalkService learningTalkService;

    public LearningTalkController(CourseService courseService, LearningTalkService learningTalkService) {
        this.courseService = courseService;
        this.learningTalkService = learningTalkService;
    }

    @GetMapping
    public String list(@PathVariable(name = "courseId") Long courseId,
                       Model model,
                       HttpSession session) {

        Course course = courseService.findById(courseId);
        List<LearningTalk> talks = learningTalkService.getAllByCourse(course);

        // 세션에 "anonymousStudentNumber" 없으면 새 번호 부여
        if (session.getAttribute("anonymousStudentNumber") == null) {
            int assignedNumber = (int)(Math.random() * 10000); // 또는 순차 번호
            session.setAttribute("anonymousStudentNumber", assignedNumber);
        }

        model.addAttribute("course", course);
        model.addAttribute("talks", talks);
        model.addAttribute("learningTalk", new LearningTalk());
        model.addAttribute("anonymousStudentNumber", session.getAttribute("anonymousStudentNumber")); // 추가
        return "learningTalk/list";
    }


    @PostMapping("/new")
    public String post(@PathVariable("courseId") Long courseId,
                       @ModelAttribute LearningTalk learningTalk,
                       HttpSession session) {

        Course course = courseService.findById(courseId);
        learningTalk.setCourse(course);
        learningTalk.setCreatedAt(LocalDateTime.now());

        Object user = session.getAttribute("loginMember");

        if (user instanceof Student student) {
            learningTalk.setStudent(student);

            // 익명번호 없으면 새로 생성
            String anonName = (String) session.getAttribute("anonymousName");
            if (anonName == null) {
                int randomNum = (int) (Math.random() * 9000) + 1000; // 1000~9999
                anonName = "학생#" + randomNum;
                session.setAttribute("anonymousName", anonName);
            }

            learningTalk.setAnonymousName(anonName);
        } else if (user instanceof Professor professor) {
            learningTalk.setProfessor(professor);
            learningTalk.setAnonymousName("교수");
        }

        learningTalkService.save(learningTalk);
        return "redirect:/courses/" + courseId + "/learning-talk";
    }
}