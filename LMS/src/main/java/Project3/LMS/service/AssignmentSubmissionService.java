package Project3.LMS.service;

import Project3.LMS.domain.Assignment;
import Project3.LMS.domain.AssignmentSubmission;
import Project3.LMS.domain.Student;
import Project3.LMS.repostiory.AssignmentRepository;
import Project3.LMS.repostiory.AssignmentSubmissionRepository;
import Project3.LMS.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AssignmentSubmissionService {

    private final AssignmentSubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final StudentService studentService;


    public AssignmentSubmission submitAssignment(Long assignmentId, String studentSid, String title, String content,
                                                 String filePath, String fileName, String fileType) {
        Assignment assignment = assignmentRepository.findById(assignmentId);
        Student student = studentService.findBySid(studentSid);

        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setId(System.currentTimeMillis());
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setTitle(title);
        submission.setContent(content);
        submission.setFile_path(filePath);
        submission.setFile_name(fileName);
        submission.setFile_type(fileType);
        submission.setSubmitted_time(LocalDateTime.now());

        student.getAssignmentSubmissions().add(submission);

        submissionRepository.save(submission);
        return submission;
    }
    public boolean isSubmitted(Student student, Assignment assignment) {
        return submissionRepository.existsByStudentAndAssignment(student, assignment);
    }

    // 특정 과제에 대한 제출 목록 조회
    public List<AssignmentSubmission> findByAssignment(Long assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId);
    }

    // 제출물 단건 조회
    public AssignmentSubmission findById(Long submissionId) {
        return submissionRepository.findById(submissionId);
    }

    // 점수 및 피드백 입력
    @Transactional
    public void gradeSubmission(Long submissionId, int grade, String feedback) {
        AssignmentSubmission submission = submissionRepository.findById(submissionId);
        if (submission != null) {
            submission.setGrade(grade);
            submission.setFeedback(feedback);
        }
    }

    public void updateSubmission(Long submissionId, String title, String content, MultipartFile file) throws IOException {
        AssignmentSubmission submission = submissionRepository.findById(submissionId);

        submission.setTitle(title);
        submission.setContent(content);

        if (file != null && !file.isEmpty()) {
            String uploadDir = System.getProperty("user.dir") + "/uploads/submissions";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = file.getOriginalFilename();
            String filePath = uploadDir + "/" + System.currentTimeMillis() + "_" + fileName;
            file.transferTo(new File(filePath));

            submission.setFile_name(fileName);
            submission.setFile_path(filePath);
            submission.setFile_type(file.getContentType());
        }

        submissionRepository.save(submission);
    }

    public AssignmentSubmission findByStudentAndAssignment(Student student, Assignment assignment) {
        return submissionRepository.findByStudentAndAssignment(student, assignment);
    }
}
