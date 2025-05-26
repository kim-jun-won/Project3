package Project3.LMS.service;

import Project3.LMS.domain.*;
import Project3.LMS.repostiory.CourseRepository;
import Project3.LMS.repostiory.EnrollmentRepository;
import Project3.LMS.repostiory.EnrollmentRequestRepository;
import Project3.LMS.repostiory.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EnrollmentRequestService {

    private final EnrollmentRequestRepository requestRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public void sendRequest(Long studentId, Long courseId, String reason) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("학생 없음"));
        Course course = courseRepository.findById(courseId);

        // 이미 수강 중인 과목인지 확인
        List<Enrollment> enrolled = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);
        if (!enrolled.isEmpty()) {
            throw new IllegalStateException("이미 수강 중인 과목입니다.");
        }

        // 중복 요청 방지
        List<EnrollmentRequest> existing = requestRepository.findByStudentId(studentId)
                .stream()
                .filter(r -> r.getCourse().getId().equals(courseId) && r.getStatus() == RequestStatus.WAITING)
                .toList();
        if (!existing.isEmpty()) {
            throw new IllegalStateException("이미 수강 승인 요청을 보낸 과목입니다.");
        }

        EnrollmentRequest request = EnrollmentRequest.createEnrollmentRequest(student, course, LocalDateTime.now(), RequestStatus.WAITING);
        request.setReason(reason);
        requestRepository.save(request);
    }

    public void approveRequest(Long requestId) {
        EnrollmentRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("요청 없음"));

        if (request.getStatus() != RequestStatus.WAITING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }

        Course course = request.getCourse();

        // 정원이 가득 찼으면 자동으로 정원(capacity)을 증가시킴
        if (course.getEnrolledCount() >= course.getCapacity()) {
            course.setCapacity(course.getEnrolledCount() + 1); // 정원 1 증가
        }

        Enrollment enrollment = Enrollment.createEnrollment(request.getStudent(), course);
        enrollmentRepository.save(enrollment);
        course.incrementEnrollment();

        request.setStatus(RequestStatus.APPROVED);
        request.setProcessedDate(LocalDateTime.now());
    }

    public void rejectRequest(Long requestId) {
        EnrollmentRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("요청 없음"));

        if (request.getStatus() != RequestStatus.WAITING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setProcessedDate(LocalDateTime.now());
    }

    public List<EnrollmentRequest> getRequestsForProfessor(Long professorId) {
        return requestRepository.findByCourseProfessorId(professorId);
    }

    public List<EnrollmentRequest> getRequestsForStudent(Long studentId) {
        return requestRepository.findByStudentId(studentId);
    }
}
