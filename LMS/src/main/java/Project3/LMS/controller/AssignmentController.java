package Project3.LMS.controller;

import Project3.LMS.domain.*;
import Project3.LMS.repostiory.AssignmentFileRepository;
import Project3.LMS.service.AssignmentQueryService;
import Project3.LMS.service.AssignmentService;
import Project3.LMS.service.AssignmentSubmissionService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;

@Controller
@RequiredArgsConstructor
@RequestMapping("/assignment")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final AssignmentFileRepository assignmentFileRepository;
    private final AssignmentSubmissionService submissionService;
    private final AssignmentQueryService assignmentQueryService;

    // 교수 - 과제 출제 폼 이동
    @GetMapping("/create")
    public String showCreateForm(@RequestParam("courseId") Long courseId, Model model) {
        model.addAttribute("courseId", courseId);
        return "assignment/createForm";
    }

    // 교수 - 과제 출제 처리
    @PostMapping("/create")
    public String createAssignment(@RequestParam("courseId") Long courseId,
                                   @RequestParam("title") String title,
                                   @RequestParam("content") String content,
                                   @RequestParam("dueDate") String dueDateStr,
                                   @RequestParam("file") MultipartFile file,
                                   HttpSession session) throws IOException {

        String professorPid = ((Professor) session.getAttribute("loginMember")).getPid();
        LocalDateTime dueDate = LocalDateTime.parse(dueDateStr);

        // 저장 디렉토리 설정
        String uploadDir = System.getProperty("user.dir") + "/uploads/assignments";
        File dir = new File(uploadDir);

// 디렉토리 없으면 생성
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            System.out.println("디렉토리 생성 여부: " + created);
        }

        // 파일 저장 경로 설정
        String fileName = file.getOriginalFilename();
        String filePath = uploadDir + "/" + System.currentTimeMillis() + "_" + fileName;
        file.transferTo(new File(filePath));

        // 과제 생성
        assignmentService.createAssignmentWithFile(
                professorPid, courseId, title, content, dueDate, fileName, filePath, file.getContentType()
        );

        return "redirect:/assignment/professor/list";
    }

    @GetMapping("/professor/list")
    public String listForProfessor(Model model, HttpSession session) {
        Professor loginProfessor = (Professor) session.getAttribute("loginMember");
        String pid = loginProfessor.getPid();
        List<Course> courses = assignmentQueryService.getCoursesByProfessorPid(pid);
        model.addAttribute("courses", courses);
        return "assignment/listForProfessor";
    }

    @GetMapping("/professor/view")
    public String viewAssignments(@RequestParam("courseId") Long courseId, Model model, HttpSession session) {
        List<Assignment> assignments = assignmentQueryService.findByCourseId(courseId);
        Course course = assignmentQueryService.findCourse(courseId);

        model.addAttribute("assignments", assignments);
        model.addAttribute("selectedCourseName", course.getCourseName());

        String pid = ((Professor) session.getAttribute("loginMember")).getPid();
        model.addAttribute("courses", assignmentQueryService.getCoursesByProfessorPid(pid));

        return "assignment/listForProfessor";
    }

    @PostMapping("/delete")
    public String deleteAssignment(@RequestParam("assignmentId") Long assignmentId) {
        assignmentService.deleteAssignment(assignmentId);
        return "redirect:/assignment/professor/list"; // 삭제 후 목록으로 리디렉션
    }

    // 교수 - 특정 과제의 제출 목록 조회 (채점용)
    @GetMapping("/professor/submissions")
    public String viewSubmissions(@RequestParam("assignmentId") Long assignmentId, Model model) {
        Assignment assignment = assignmentQueryService.findById(assignmentId);
        List<AssignmentSubmission> submissions = submissionService.findByAssignment(assignmentId);

        model.addAttribute("assignment", assignment);
        model.addAttribute("submissions", submissions);
        return "assignment/listSubmissions";
    }

    // 교수 - 채점 폼 이동
    @GetMapping("/professor/grade")
    public String showGradeForm(@RequestParam("submissionId") Long submissionId, Model model) {
        AssignmentSubmission submission = submissionService.findById(submissionId);
        model.addAttribute("submission", submission);
        return "assignment/gradeForm";
    }

    // 교수 - 점수 등록 처리
    @PostMapping("/professor/grade")
    public String gradeSubmission(@RequestParam("submissionId") Long submissionId,
                                  @RequestParam("grade") int grade,
                                  @RequestParam(value = "feedback", required = false) String feedback) {
        submissionService.gradeSubmission(submissionId, grade, feedback);
        Long assignmentId = submissionService.findById(submissionId).getAssignment().getId();
        return "redirect:/assignment/professor/submissions?assignmentId=" + assignmentId;
    }



    // 학생 - 과제 제출 폼 이동
    @GetMapping("/submit")
    public String showSubmitForm(@RequestParam("assignmentId") Long assignmentId, Model model) {
        model.addAttribute("assignmentId", assignmentId);
        return "assignment/submitForm";
    }

    // 학생 - 과제 제출 처리
    @PostMapping("/submit")
    public String submitAssignment(@RequestParam("assignmentId") Long assignmentId,
                                   @RequestParam("title") String title,
                                   @RequestParam("content") String content,
                                   @RequestParam("file") MultipartFile file,
                                   HttpSession session) throws IOException {

        Student student = (Student) session.getAttribute("loginMember");
        String studentSid = student.getSid();
        String fileName = file.getOriginalFilename();

        String uploadDir = System.getProperty("user.dir") + "/uploads/submissions";
        new File(uploadDir).mkdirs();
        String filePath = uploadDir + "/" + System.currentTimeMillis() + "_" + fileName;
        file.transferTo(new File(filePath));

        submissionService.submitAssignment(assignmentId, studentSid, title, content, filePath, fileName, file.getContentType());
        return "redirect:/assignment/student/list";
    }

    @GetMapping("/student/list")
    public String listCoursesForStudent(Model model, HttpSession session) {
        Student student = (Student) session.getAttribute("loginMember");
        List<Course> courses = assignmentQueryService.getCoursesByStudentSid(student.getSid());
        model.addAttribute("courses", courses);
        return "assignment/listForStudent";
    }

    @GetMapping("/student/viewList")
    public String viewAssignmentList(@RequestParam("courseId") Long courseId, Model model, HttpSession session) {
        Student student = (Student) session.getAttribute("loginMember");
        List<Assignment> assignments = assignmentQueryService.findByCourseId(courseId);
        Course course = assignmentQueryService.findCourse(courseId);
        List<Course> courses = assignmentQueryService.getCoursesByStudentSid(student.getSid());

        Map<Long, Boolean> submissionStatus = new HashMap<>();
        for (Assignment assignment : assignments) {
            boolean submitted = submissionService.isSubmitted(student, assignment); // ✅
            submissionStatus.put(assignment.getId(), submitted);
        }
        model.addAttribute("courses", courses);
        model.addAttribute("assignments", assignments);
        model.addAttribute("selectedCourseName", course.getCourseName());
        model.addAttribute("submissionStatus", submissionStatus);
        return "assignment/listForStudent";
    }

    @GetMapping("/student/detail")
    public String viewAssignmentDetail(@RequestParam("assignmentId") Long assignmentId, Model model, HttpSession session) {
        Assignment assignment = assignmentQueryService.findById(assignmentId);        Course course = assignment.getCourse();
        Student student = (Student) session.getAttribute("loginMember");
        List<Course> courses = assignmentQueryService.getCoursesByStudentSid(student.getSid());
        List<Assignment> assignments = assignmentQueryService.findByCourseId(course.getId());


        model.addAttribute("courses", courses);
        model.addAttribute("assignments", assignments);
        model.addAttribute("selectedCourseName", course.getCourseName());
        model.addAttribute("selectedAssignment", assignment);
        return "assignment/listForStudent";
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<org.springframework.core.io.Resource> downloadFile(@PathVariable("fileId") Long fileId) throws IOException {
        AssignmentFile file = assignmentFileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));

        // Get the file path
        Path path = Paths.get(file.getFile_path());
        // Create a UrlResource object
        org.springframework.core.io.Resource resource = new UrlResource(path.toUri());

        // Return the ResponseEntity with the appropriate headers and content type
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFile_name() + "\"")
                .body(resource);
    }

}