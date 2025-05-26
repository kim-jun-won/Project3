package Project3.LMS.controller;

import Project3.LMS.domain.EnrollmentRequest;
import Project3.LMS.domain.Professor;
import Project3.LMS.domain.Student;
import Project3.LMS.service.EnrollmentRequestService;
import Project3.LMS.service.EnrollmentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EnrollmentRequestController {

    private final EnrollmentRequestService enrollmentRequestService;
    private final EnrollmentService enrollmentService;

    /**
     * 전체 수강 요청 리스트 조회
     * */
    @GetMapping("/requests/professor/all-requests")
    public String viewAllRequestsForProfessor(HttpSession session, Model model) {
        Object loginMember = session.getAttribute("loginMember");
        if (!(loginMember instanceof Professor professor)) {
            return "accessDenied";
        }

        List<EnrollmentRequest> requests = enrollmentRequestService.getRequestsForProfessor(professor.getId());
        model.addAttribute("requests", requests);
        return "request/allRequests"; // 템플릿 경로
    }

    /**
     * 교수 - 해당 과목의 수강 요청 리스트 조회
     */
    @GetMapping("/courses/{courseId}/requests")
    public String viewRequestList(@PathVariable("courseId") Long courseId,
                                  Model model,
                                  HttpSession session) {

        Object loginMember = session.getAttribute("loginMember");
        if (!(loginMember instanceof Professor professor)) {
            return "accessDenied";
        }

        List<EnrollmentRequest> requests = enrollmentRequestService.getRequestsForProfessor(professor.getId());
        model.addAttribute("requests", requests);
        model.addAttribute("courseId", courseId);

        return "request/requestList"; // 템플릿: /templates/request/requestList.html
    }

    /**
     * 교수 - 수강 요청 승인
     */
    @PostMapping("/courses/{courseId}/requests/{requestId}/approve")
    public String approveRequest(@PathVariable("courseId") Long courseId,
                                 @PathVariable("requestId") Long requestId,
                                 HttpSession session) {

        if (!(session.getAttribute("loginMember") instanceof Professor)) {
            return "accessDenied";
        }

        enrollmentRequestService.approveRequest(requestId);
        return "redirect:/courses/" + courseId + "/requests";
    }

    /**
     * 교수 - 수강 요청 거절
     */
    @PostMapping("/courses/{courseId}/requests/{requestId}/reject")
    public String rejectRequest(@PathVariable("courseId") Long courseId,
                                @PathVariable("requestId") Long requestId,
                                HttpSession session) {

        if (!(session.getAttribute("loginMember") instanceof Professor)) {
            return "accessDenied";
        }

        enrollmentRequestService.rejectRequest(requestId);
        return "redirect:/courses/" + courseId + "/requests";
    }

    /**
     * 학생 - 수강 승인 요청 사유 입력 폼으로 이동
     */
    @GetMapping("/courses/{courseId}/requests/new-form")
    public String showRequestForm(@PathVariable("courseId") Long courseId,
                                  Model model,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Object loginMember = session.getAttribute("loginMember");
        if (!(loginMember instanceof Student)) {
            return "accessDenied";
        }

        boolean alreadyEnrolled = enrollmentService.isAlreadyEnrolled(((Student) loginMember).getId(), courseId);
        if (alreadyEnrolled) {
            redirectAttributes.addFlashAttribute("errorMessage", "이미 수강 중인 과목입니다.");
            return "redirect:/enroll";
        }

        model.addAttribute("courseId", courseId);
        return "request/sendRequestForm";
    }

    /**
     * 학생 - 수강 요청 전송
     */
    @PostMapping("/courses/{courseId}/requests/new")
    public String sendRequest(@PathVariable("courseId") Long courseId,
                              @RequestParam(required = false) String reason,
                              HttpSession session) {

        Object loginMember = session.getAttribute("loginMember");
        if (!(loginMember instanceof Student student)) {
            return "accessDenied";
        }

        enrollmentRequestService.sendRequest(student.getId(), courseId, reason);
        return "redirect:/enroll"; // 혹은 이전 수강 신청 페이지
    }

    /**
     * 학생 - 나의 수강 요청 내역 보기
     */
    @GetMapping("/courses/{courseId}/requests/my")
    public String viewMyRequests(@PathVariable("courseId") Long courseId,
                                 Model model,
                                 HttpSession session) {

        Object loginMember = session.getAttribute("loginMember");
        if (!(loginMember instanceof Student student)) {
            return "accessDenied";
        }

        List<EnrollmentRequest> requests = enrollmentRequestService.getRequestsForStudent(student.getId());
        model.addAttribute("requests", requests);
        model.addAttribute("courseId", courseId);

        return "request/myRequestStatus"; // 템플릿: /templates/request/myRequestStatus.html
    }
}
