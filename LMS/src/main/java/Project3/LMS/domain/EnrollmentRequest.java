package Project3.LMS.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class EnrollmentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 요청한 학생
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    // 요청 대상 과목
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    // 요청 사유 (선택 입력)
    @Column(length = 1000)
    private String reason;

    // 요청한 날짜
    private LocalDateTime requestDate;
    // 처리 일시 (승인/거절 시)
    private LocalDateTime processedDate;

    // 상태: 대기 / 승인 / 거절
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    public static EnrollmentRequest createEnrollmentRequest(Student student, Course course, LocalDateTime enrollmentDate, RequestStatus enrollmentStatus) {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
        enrollmentRequest.student = student;
        enrollmentRequest.course = course;
        enrollmentRequest.requestDate = enrollmentDate;
        enrollmentRequest.status = enrollmentStatus;

        return enrollmentRequest;
    }

}
