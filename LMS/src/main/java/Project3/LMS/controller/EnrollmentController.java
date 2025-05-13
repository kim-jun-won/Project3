package Project3.LMS.controller;

import Project3.LMS.domain.Course;
import Project3.LMS.domain.Enrollment;
import Project3.LMS.domain.Student;
import Project3.LMS.repostiory.CourseSearch;
import Project3.LMS.repostiory.EnrollmentRepository;
import Project3.LMS.service.CourseService;
import Project3.LMS.service.EnrollmentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/enroll")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseService courseService;

    @GetMapping
    public String createForm(@ModelAttribute("courseSearch") CourseSearch courseSearch,
                             Model model,
                             HttpSession session) {

        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId == null) {
            return "redirect:/login"; // 로그인 안 되어 있으면 로그인 페이지로 이동
        }

        List<Course> courses = courseService.findCourses(courseSearch);
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);

        model.addAttribute("enrollments", enrollments);
        model.addAttribute("courses", courses);
        model.addAttribute("studentId", studentId);

        return "/enrollment/enrollForm";
    }

    @PostMapping
    public String enroll(@RequestParam("studentId") Long studentId,
                         @RequestParam("courseId") Long courseId,
                         Model model,
                         @ModelAttribute("courseSearch") CourseSearch courseSearch,
                         HttpSession session) {

        try {
            enrollmentService.enroll(studentId, courseId);
            return "redirect:/enroll?studentId=" + studentId;

        } catch (IllegalStateException e) {
            List<Course> courses = courseService.findCourses(courseSearch);
            List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);

            model.addAttribute("enrollments", enrollments);
            model.addAttribute("courses", courses);
            model.addAttribute("studentId", studentId);
            model.addAttribute("errorMessage", e.getMessage());

            return "/enrollment/enrollForm";
        }
    }

    @PostMapping("/cancel")
    public String cancel(@RequestParam("enrollmentId") Long enrollmentId, HttpSession session) {

        enrollmentService.cancelEnroll(enrollmentId);
        Long studentId = ((Student) session.getAttribute("loginMember")).getId();
        return "redirect:/enroll?studentId=" + studentId;
    }



}
