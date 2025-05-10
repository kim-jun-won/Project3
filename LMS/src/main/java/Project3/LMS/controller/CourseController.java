package Project3.LMS.controller;

import Project3.LMS.CourseForm;
import Project3.LMS.domain.Course;
import Project3.LMS.domain.Professor;
import Project3.LMS.repostiory.CourseSearch;
import Project3.LMS.service.CourseService;
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
        model.addAttribute("courseForm", new CourseForm());
        return "course/createCourseForm";
    }

    // 강의 등록 처리
    @PostMapping("/new")
    public String create(@ModelAttribute("courseForm") CourseForm form) {
        Course course = new Course();
        course.setCourseName(form.getCourseName());
        course.setCredits(form.getCredits());

        //임시 교수 생성 (나중에 만들기)
        Professor professor = em.find(Professor.class, form.getProfessorId());
        course.setProfessor(professor);  // 이게 영속 상태 연결
        course.setProfessor(professor);
        ///
        courseService.registerCourse(course);
        return "redirect:/course-manage";
    }

    // 강의 삭제
    @PostMapping("/delete")
    public String deleteCourse(@RequestParam("courseId") Long courseId) {
        courseService.deleteCourse(courseId);
        return "redirect:/course-manage";
    }



}
