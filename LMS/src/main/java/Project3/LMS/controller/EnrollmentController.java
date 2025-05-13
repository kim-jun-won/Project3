package Project3.LMS.controller;

import Project3.LMS.domain.*;
import Project3.LMS.dto.GradeInputDTO;
import Project3.LMS.repostiory.CourseRepository;
import Project3.LMS.repostiory.CourseSearch;
import Project3.LMS.repostiory.EnrollmentRepository;
import Project3.LMS.service.CourseService;
import Project3.LMS.service.EnrollmentService;
import Project3.LMS.service.TimetableService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/enroll")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseService courseService;
    private final CourseRepository courseRepository;
    private final TimetableService timetableService;

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

    @GetMapping("/gradeCheck")
    public String gradeCheck(HttpSession session, Model model) {
        // 세션에서 로그인한 학생 객체 가져오기
        Student student = (Student) session.getAttribute("loginMember");

        if (student == null) {
            // 세션에 로그인한 학생이 없으면 로그인 페이지로 리다이렉트
            return "redirect:/login";
        }

        // 학생의 수강 목록 가져오기
        List<Enrollment> studentEnrollList = enrollmentRepository.findByStudentId(student.getId());

        // 수강 목록이 없을 경우 메시지 추가
        if (studentEnrollList.isEmpty()) {
            model.addAttribute("message", "수강 중인 과목이 없습니다.");
        }

        // 모델에 수강 목록 추가
        model.addAttribute("studentEnrollList", studentEnrollList);
        return "enrollment/gradeCheck"; // enrollment/gradeCheck.html 템플릿으로 이동
    }

    /**
     *
     * 수강 성적 입력 페이지
     */
    @GetMapping("/attendance")
    public String attendance(HttpSession session, Model model) {
        Professor professor = (Professor) session.getAttribute("loginMember");
        if (professor == null) return "redirect:/login";

        List<Course> courseList = courseRepository.findByProfessor(professor);

        Map<Course, List<GradeInputDTO>> courseEnrollMap = new LinkedHashMap<>();
        for (Course course : courseList) {
            List<Enrollment> enrollments = enrollmentRepository.findByCourse(course);
            List<GradeInputDTO> dtos = enrollments.stream().map(e -> {
                GradeInputDTO dto = new GradeInputDTO();
                dto.setStudentId(e.getStudent().getId());
                dto.setStudentName(e.getStudent().getName());
                dto.setGrade(e.getGrade());
                return dto;
            }).toList();
            courseEnrollMap.put(course, dtos);
        }

        model.addAttribute("courseEnrollMap", courseEnrollMap);
        return "enrollment/attendance";
    }

    @PostMapping("/attendance/save")
    @Transactional
    public String saveAllGrades(@RequestParam("courseIds") List<Long> courseIds,
                                @RequestParam("studentIds") List<Long> studentIds,
                                @RequestParam("grades") List<String> grades,
                                RedirectAttributes redirectAttributes) {
        for (int i = 0; i < studentIds.size(); i++) {
            String grade = grades.get(i);
            if (grade != null && !grade.isBlank()) {
                Long courseId = courseIds.get(i);
                Long studentId = studentIds.get(i);
                Enrollment enrollment = enrollmentRepository.findByCourseIdAndStudentId2(courseId, studentId);
                enrollment.setGrade(grade);
                enrollmentRepository.save(enrollment);
            }
        }
        redirectAttributes.addFlashAttribute("saved", true);
        return "redirect:/enroll/attendance";
    }







}
