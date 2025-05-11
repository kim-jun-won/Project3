package Project3.LMS.controller;

import Project3.LMS.CourseForm;
import Project3.LMS.domain.Course;
import Project3.LMS.domain.Professor;
import Project3.LMS.repostiory.CourseSearch;
import Project3.LMS.repostiory.ProfessorRepository;
import Project3.LMS.service.CourseService;
import Project3.LMS.service.ProfessorService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/course-manage")
public class CourseController {

    private final CourseService courseService;
    private final ProfessorService professorService;
    private final ProfessorRepository professorRepository;
    EntityManager em;


    // 강의 목록 조회
    @GetMapping
    public String courseManage(@ModelAttribute("courseSearch") CourseSearch courseSearch, Model model) {
        List<Course> courses = courseService.findCourses(courseSearch);
        model.addAttribute("courses", courses);
        return "course/courseManage";
    }

    // 강의 등록 폼으로 이동
    @GetMapping("/new")
    public String createForm(Model model) {
        List<Professor> professors = professorService.findAll();

        model.addAttribute("courseForm", new CourseForm());
        model.addAttribute("professors", professors);
        return "course/createCourseForm";
    }

    // 강의 등록 처리
    @PostMapping("/new")
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
    @PostMapping("/delete")
    public String deleteCourse(@RequestParam(value = "courseId", required = false) Long courseId) {
        System.out.println("넘어온 courseId = " + courseId); // 로그 찍기

        if (courseId != null) {
            courseService.deleteCourse(courseId);
        } else {
            System.out.println("선택된 강의 없음!");
        }

        return "redirect:/course-manage";
    }



}
