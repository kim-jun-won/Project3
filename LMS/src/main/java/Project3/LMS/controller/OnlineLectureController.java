package Project3.LMS.controller;

import Project3.LMS.domain.Course;
import Project3.LMS.domain.Enrollment;
import Project3.LMS.domain.Professor;
import Project3.LMS.domain.Student;
import Project3.LMS.dto.OnlineLectureDTO;
import Project3.LMS.repostiory.CourseRepository;
import Project3.LMS.repostiory.CourseSearch;
import Project3.LMS.repostiory.EnrollmentRepository;
import Project3.LMS.service.CourseService;
import Project3.LMS.service.OnlineLectureService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OnlineLectureController {

    private final CourseService courseService;
    private final OnlineLectureService onlineLectureService;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;


    //온라인강의 등록 폼으로 이동
    @GetMapping("/online-lecture")
    public String createOnlineLectureForm(HttpSession session, Model model) {
        Professor loginProfessor = (Professor) session.getAttribute("loginMember");

        CourseSearch courseSearch = new CourseSearch();
        courseSearch.setProfessorName(loginProfessor.getName());
        List<Course> courses = courseRepository.findAll(courseSearch);
        model.addAttribute("courses", courses);

        return "course/onlineLectureForm";
    }

    //URL 입력 폼으로 이동
    @GetMapping("/course/{courseId}/online-lecture/new")
    public String createRegisterOnlineLectureForm(@PathVariable Long courseId, Model model) {
        Course course = courseService.findbyCourseId(courseId);
        model.addAttribute("course", course);
        model.addAttribute("onlineLectures", course.getOnlineLectures());
        model.addAttribute("onlineLectureDTO", new OnlineLectureDTO());
        return "course/registerOnlineLectureForm";  // 이 HTML 파일이 존재해야 함
    }

    //URl 저장
    @PostMapping("/course/{courseId}/online-lecture")
    public String registerOnlineLecture(@PathVariable Long courseId,
                                        @ModelAttribute OnlineLectureDTO onlineLectureDTO) {
        onlineLectureService.addOnlineLecture(onlineLectureDTO, courseId);
        return "redirect:/course/" + courseId + "/online-lecture/new";
    }

    //학생의 수강 강의 목록으로 이동 (
    @GetMapping("/content")
    public String createOnlineLectureView(HttpSession session, Model model) {
        Student student = (Student) session.getAttribute("loginMember");
        Long studentId = student.getId();

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);

        model.addAttribute("enrollments", enrollments);

        return "/course/viewOnlineLecture";

    }

    //특정 강의의 온라인 강의 목록 조회 및 시청
    @GetMapping("/content/{courseId}/online-lectures")
    public String createOnlineLectureListView(@PathVariable Long courseId, Model model) {
        Course course = courseService.findbyCourseId(courseId);
        model.addAttribute("course", course);
        model.addAttribute("onlineLectures", course.getOnlineLectures());
        return "course/onlineLectureList";
    }
}
