package Project3.LMS.controller;

import Project3.LMS.CourseForm;
import Project3.LMS.domain.Course;
import Project3.LMS.domain.Enrollment;
import Project3.LMS.domain.Professor;
import Project3.LMS.domain.Student;
import Project3.LMS.repostiory.CourseRepository;
import Project3.LMS.repostiory.CourseSearch;
import Project3.LMS.repostiory.EnrollmentRepository;
import Project3.LMS.repostiory.ProfessorRepository;
import Project3.LMS.service.CourseService;
import Project3.LMS.service.EnrollmentService;
import Project3.LMS.service.ProfessorService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final ProfessorService professorService;
    private final ProfessorRepository professorRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentService enrollmentService;
    private final EnrollmentRepository enrollmentRepository;
    EntityManager em;


    // 강의 목록 조회
    @GetMapping("/course-manage")
    public String courseManage(@ModelAttribute("courseSearch") CourseSearch courseSearch, Model model) {
        List<Course> courses = courseService.findCourses(courseSearch);
        model.addAttribute("courses", courses);
        return "course/courseManage";
    }

    // 강의 등록 폼으로 이동
    @GetMapping("/course-manage/new")
    public String createForm(Model model) {
        List<Professor> professors = professorService.findAll();

        model.addAttribute("courseForm", new CourseForm());
        model.addAttribute("professors", professors);
        return "course/createCourseForm";
    }

    // 강의 등록 처리
    @PostMapping("/course-manage/new")
    public String create(@ModelAttribute("courseForm") CourseForm form,
                         Model model) {

        try {
            Professor professor = professorRepository.findById(form.getProfessorId())
                    .orElseThrow(() -> new IllegalArgumentException("교수를 찾을 수 없습니다."));

            Course course = Course.createCourse(form.getCourseName(), form.getCredits(), professor);
            courseService.registerCourse(course);
            return "redirect:/course-manage";

        } catch (IllegalStateException e) {
            // 중복 강의명 예외 처리
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("courseForm", form);
            model.addAttribute("professors", professorRepository.findAll());
            return "course/createCourseForm";
        }
    }

    // 강의 삭제
    @PostMapping("/course-manage/delete")
    public String deleteCourse(@RequestParam(value = "courseId", required = false) Long courseId) {

        if (courseId != null) {
            courseService.deleteCourse(courseId);
        } else {
            System.out.println("선택된 강의가 없습니다.");
        }

        return "redirect:/course-manage";
    }


}
