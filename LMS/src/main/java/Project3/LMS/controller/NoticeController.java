package Project3.LMS.controller;

import Project3.LMS.domain.*;
import Project3.LMS.service.CourseService;
import Project3.LMS.service.NoticeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class NoticeController {
    private final NoticeService noticeService;
    private final CourseService courseService;

    /**
     * 교수용 - 공지사항 목록 조회
     * */
    @GetMapping("/notice/professor/list")
    public String listProfessoNotices(HttpSession session, Model model){
        Professor professor = (Professor)session.getAttribute("loginMember");
        if(professor==null) return "redirect:/login";

        List<Course> courseList = courseService.getCoursesByProfessor(professor.getId());
        List<Notice> noticeList = noticeService.getProfessorNotices(courseList);

        model.addAttribute("notices", noticeList);
            return "notice/professorList";
    }

    /**
     * 교수용 - 공지사항 등록 폼
     * */
    @GetMapping("/notice/professor/new")
    public String showCreateForm(HttpSession session, Model model) {
        Professor professor = (Professor) session.getAttribute("loginMember");
        if (professor == null) return "redirect:/login";

        List<Course> courses = courseService.getCoursesByProfessor(professor.getId());
        model.addAttribute("courses", courses);
        model.addAttribute("noticeForm", new NoticeForm());

        return "notice/noticeForm";
    }

    /**
     * 교수용 - 공지사항 등록 처리
     * */
    @PostMapping("/notice/professor/new")
    public String createNotice(@ModelAttribute NoticeForm form, HttpSession session) {
        Professor professor = (Professor) session.getAttribute("loginMember");
        if (professor == null) return "redirect:/login";

        noticeService.createByProfessor(form.getCourseId(), professor, form.getTitle(), form.getContent());

        return "redirect:/notice/professor/list";
    }

    /**
     * 교수용 - 공지사항 수정 폼
     * */
    @GetMapping("/notice/professor/edit/{noticeId}")
    public String showEditForm(@PathVariable Long noticeId, Model model, HttpSession session) {
        Professor professor = (Professor) session.getAttribute("loginMember");
        if (professor == null) return "redirect:/login";

        Notice notice = noticeService.getNoticeById(noticeId);
        if (!notice.getProfessor().getId().equals(professor.getId())) {
            return "redirect:/notice/professor/list";
        }

        NoticeForm form = new NoticeForm();
        form.setNoticeId(notice.getId());
        form.setCourseId(notice.getCourse().getId());
        form.setTitle(notice.getTitle());
        form.setContent(notice.getContent());

        model.addAttribute("noticeForm", form);
        model.addAttribute("isEdit", true);

        // 과목 목록 + 선택된 과목 추가
        List<Course> courses = courseService.getCoursesByProfessor(professor.getId());
        model.addAttribute("courses", courses);
        model.addAttribute("selectedCourse", notice.getCourse());

        return "notice/noticeForm";
    }


    /**
     * 교수용 - 공지사항 수정 처리
     * */
    @PostMapping("/notice/professor/edit")
    public String updateNotice(@ModelAttribute NoticeForm form, HttpSession session) {
        Professor professor = (Professor) session.getAttribute("loginMember");
        if (professor == null) return "redirect:/login";

        noticeService.updateNotice(form.getNoticeId(), professor, form.getTitle(), form.getContent());

        return "redirect:/notice/professor/list";
    }


    /**
     * 교수용 - 공지사항 삭제
     * */
    @PostMapping("/notice/professor/delete/{noticeId}")
    public String deleteNotice(@PathVariable Long noticeId, HttpSession session) {
        Professor professor = (Professor) session.getAttribute("loginMember");
        if (professor == null) return "redirect:/login";

        noticeService.deleteNotice(noticeId, professor);

        return "redirect:/notice/professor/list";
    }


    /**
     * 학생: 수강 중인 과목의 공지사항 리스트 조회
     */
    @GetMapping("/notice/student/list")
    public String listStudentNotices(HttpSession session, Model model) {
        Student student = (Student) session.getAttribute("loginMember");
        if (student == null) return "redirect:/login";

        List<Notice> noticeList = noticeService.findStudentNotices(student);

        model.addAttribute("notices", noticeList);
        return "notice/studentNoticeList";
    }

    /**
     * 관리자: 시스템 공지사항 리스트 조회
     */
    @GetMapping("/notice/admin/list")
    public String listAdminNotices(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("loginMember");
        if (admin == null) return "redirect:/login";

        List<Notice> noticeList = noticeService.findAdminNotices();

        model.addAttribute("notices", noticeList);
        return "notice/adminNoticeList";
    }

}
