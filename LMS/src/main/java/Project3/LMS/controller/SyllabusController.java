package Project3.LMS.controller;

import Project3.LMS.domain.Course;
import Project3.LMS.domain.Professor;
import Project3.LMS.domain.Student;
import Project3.LMS.domain.Syllabus;
import Project3.LMS.service.CourseService;
import Project3.LMS.service.SyllabusService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SyllabusController {
    private final SyllabusService syllabusService;
    private final CourseService courseService;

    /**
     * 교수용 강의계획서 조회
     */
    @GetMapping("/syllabus/professor/list")
    public String listSyllabus(HttpSession session, Model model) {
        Professor professor = (Professor) session.getAttribute("loginMember");
        if (professor == null) return "redirect:/login";

        List<Course> courseList = courseService.getCoursesByProfessor(professor.getId());
        model.addAttribute("courses", courseList);
        return "syllabus/syllabusList";
    }

    /**
     * 교수용 강의계획서 등록 폼
     */
    @GetMapping("/syllabus/professor/new")
    public String createForm(@RequestParam Long courseId, HttpSession session, Model model) {
        Professor professor = (Professor) session.getAttribute("loginMember");
        Course course = courseService.getCourse(courseId);
        if (!course.getProfessor().getId().equals(professor.getId())) {
            throw new IllegalArgumentException("해당 교수의 과목이 아닙니다.");
        }

        model.addAttribute("course", course);
        return "syllabus/createForm";
    }

    /**
     * 교수용 강의계획서 등록 처리
     */
    @PostMapping("/syllabus/professor/new")
    public String create(@RequestParam Long courseId, @RequestParam String content, HttpSession session) {
        Professor professor = (Professor) session.getAttribute("loginMember");
        Course course = courseService.getCourse(courseId);
        if (!course.getProfessor().getId().equals(professor.getId())) {
            throw new IllegalArgumentException("해당 교수의 과목이 아닙니다.");
        }

        syllabusService.createSyllabus(courseId, content);
        return "redirect:/syllabus/professor/list";
    }

    /**
     * 교수용 강의계획서 수정 폼
     */
    @GetMapping("/syllabus/professor/{courseId}/edit")
    public String editForm(@PathVariable Long courseId, HttpSession session, Model model) {
        Professor professor = (Professor) session.getAttribute("loginMember");
        Course course = courseService.getCourse(courseId);
        if (!course.getProfessor().getId().equals(professor.getId())) {
            throw new IllegalArgumentException("해당 교수의 과목이 아닙니다.");
        }

        Syllabus syllabus = syllabusService.getSyllabusbyCourseId(courseId);
        model.addAttribute("course", course);
        model.addAttribute("syllabus", syllabus);
        return "syllabus/editForm";
    }

    /**
     * 교수용 강의계획서 수정 처리
     */
    @PostMapping("/syllabus/professor/{courseId}/edit")
    public String edit(@PathVariable Long courseId, @RequestParam String content, HttpSession session) {
        Professor professor = (Professor) session.getAttribute("loginMember");
        Course course = courseService.getCourse(courseId);
        if (!course.getProfessor().getId().equals(professor.getId())) {
            throw new IllegalArgumentException("해당 교수의 과목이 아닙니다.");
        }

        syllabusService.updateSyllabusByCourseId(courseId, content);
        return "redirect:/syllabus/professor/list";
    }

    /**
     * 교수용 강의계획서 삭제 처리
     */
    @PostMapping("/syllabus/professor/{courseId}/delete")
    public String delete(@PathVariable Long courseId, HttpSession session) {
        Professor professor = (Professor) session.getAttribute("loginMember");
        Course course = courseService.getCourse(courseId);
        if (!course.getProfessor().getId().equals(professor.getId())) {
            throw new IllegalArgumentException("해당 교수의 과목이 아닙니다.");
        }
        System.out.println("-----------------------");
        syllabusService.deleteSyllabus(courseId);
        System.out.println("-----------------------");
        return "redirect:/syllabus/professor/list";
    }

    /**
     * 학생용 강의계획서 리스트
     */
    @GetMapping("/syllabus/student/list")
    public String studentSyllabusList(HttpSession session, Model model) {
        Student student = (Student) session.getAttribute("loginMember");
        if (student == null) return "redirect:/login";

        List<Course> enrolledCourses = syllabusService.getCoursesByStudent(student.getId());
        model.addAttribute("courses", enrolledCourses);
        return "syllabus/studentSyllabusList";
    }

    /**
     * 학생용 강의계획서 상세
     */
    @GetMapping("/syllabus/student/{courseId}")
    public String viewSyllabus(@PathVariable Long courseId, Model model) {
        Syllabus syllabus = syllabusService.getSyllabusbyCourseId(courseId);
        model.addAttribute("syllabus", syllabus);
        return "syllabus/viewSyllabus";
    }

}
