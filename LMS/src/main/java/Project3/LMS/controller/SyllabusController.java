package Project3.LMS.controller;

import Project3.LMS.domain.Course;
import Project3.LMS.domain.Syllabus;
import Project3.LMS.repostiory.SyllabusRepository;
import Project3.LMS.service.CourseService;
import Project3.LMS.service.SyllabusService;
import lombok.Getter;
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
     * 교수용 page
     */
    // RequestParam -> ?professorId=5에서 5를 자동으로 받아서 professorId 변수에 저장해줌
    @GetMapping("/syllabus-manage")
    public String redirectProfessor(@RequestParam Long professorId){
        return "redirect:/syllabus/professor/list?professorId=" + professorId;
    }

    /**
     * 교수용 강의계획서 조회
     */
    @GetMapping("/syllabus/professor/list")
    public String listSyllabus(@RequestParam Long professorId, Model model){
        List<Course> courseList = courseService.getCoursesByProfessor(professorId);
        model.addAttribute("courses", courseList);
        model.addAttribute("professorId", professorId);
        return "syllabus/syllabusList";
    }

    /**
     * 교수용 강의계획서 등록 폼
     * */
    @GetMapping("/syllabus/professor/new")
    public String createForm(@RequestParam Long professorId, Model model){
        //해당 교수의 과목만 조회
        List<Course> myCourses = courseService.getCoursesByProfessor(professorId);
        model.addAttribute("courses",myCourses);
        model.addAttribute("professorId",professorId);
        return "syllabus/createForm";
    }

    /**
     * 교수용 강의계획서 등록 폼 페이지
     * 기존 페이지 불러와서 수정 기능 -> POST 방식 이용
     * */
    @PostMapping("/syllabus/professor/new")
    public String create(@RequestParam Long professorId, @RequestParam Long courseId, @RequestParam String content){
        Course course = courseService.getCourse(courseId);
        if(!course.getProfessor().getId().equals(professorId)){
            throw new IllegalArgumentException("이 과목은 해당 교수가 담당하지 않습니다.!");
        }

        syllabusService.createSyllabus(courseId, content);
        return "redirect:/syllabus/professor/list?professorId=" + professorId;
    }

    /**
     * 강의계획서 수정 처리
     * 기존내용 불러와 수정처리
     * */
    @GetMapping("/syllabus/professor/{courseId}/edit")
    public String editForm(@PathVariable Long courseId, @RequestParam Long professorId, Model model){
        Course course = courseService.getCourse(courseId);

        // 해당 교수인지 검증
        if (!course.getProfessor().getId().equals(professorId)) {
            throw new IllegalArgumentException("이 과목은 해당 교수가 담당하지 않습니다.");
        }

        Syllabus syllabus = syllabusService.getSyllabusbyCourseId(courseId);
        model.addAttribute("syllabus",syllabus);
        model.addAttribute("professorId",professorId);
        return "syllabus/editForm";
    }

    /**
     * 강의계획서 수정 처리
     */
    @PostMapping("/syllabus/professor/{courseId}/edit")
    public String edit(@PathVariable Long courseId,
                       @RequestParam Long professorId,
                       @RequestParam String content) {
        Course course = courseService.getCourse(courseId);

        // 교수 본인 과목인지 검증
        if (!course.getProfessor().getId().equals(professorId)) {
            throw new IllegalArgumentException("이 과목은 해당 교수가 담당하지 않습니다.");
        }

        syllabusService.updateSyllabus(courseId, content);
        return "redirect:/syllabus/professor/list?professorId=" + professorId;
    }

    /**
     * 강의계획서 삭제 처리
     */
    @PostMapping("/syllabus/professor/{courseId}/delete")
    public String delete(@PathVariable Long courseId,
                         @RequestParam Long professorId) {
        Course course = courseService.getCourse(courseId);

        // 검증
        if (!course.getProfessor().getId().equals(professorId)) {
            throw new IllegalArgumentException("이 과목은 해당 교수가 담당하지 않습니다.");
        }

        syllabusService.deleteSyllabus(courseId);
        return "redirect:/syllabus/professor/list?professorId=" + professorId;
    }

    /**
     * 학새용 사이드마 메뉴 (syllabus)
     * */
    @GetMapping("/syllabus")
    public String redirectStudnet(@RequestParam Long studentId){
        return "redirect:/syllabus/student/list?studentId="+studentId;
    }

    /**
     * 학생이 수강 중인 과목의 강의계획서 리스트 출력
     */
    @GetMapping("/syllabus/student/list")
    public String studentSyllabusList(@RequestParam Long studentId, Model model) {
        List<Course> enrolledCourses = syllabusService.getCoursesByStudent(studentId);
        model.addAttribute("courses", enrolledCourses);
        return "syllabus/studentSyllabusList"; // View: 학생용 리스트
    }

    /**
     * 특정 과목의 강의계획서 상세 보기 (학생용)
     */
    @GetMapping("/syllabus/student/{courseId}")
    public String viewSyllabus(@PathVariable Long courseId, Model model) {
        Syllabus syllabus = syllabusService.getSyllabusbyCourseId(courseId);
        model.addAttribute("syllabus", syllabus);
        return "syllabus/viewSyllabus"; // View: 상세 보기 페이지
    }




}
