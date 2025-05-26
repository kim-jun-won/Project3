package Project3.LMS.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 수강신청 엔티티
 */
@Entity
@Table(name = "enrollment", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "course_id"})
})
@Getter @Setter
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id",nullable = false)
    private Course course;

    private LocalDate enrollmentDate;

    private String grade;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus enrollmentsStatus;


    /**
     *     연관관계 메소드
     */
    public void setStudent(Student student) {
        this.student = student;
        student.getEnrollments().add(this);
    }

    public void setCourse(Course course) {
        this.course = course;
        course.getEnrollments().add(this);
    }

    /**
     *     연관관계 메소드
     */
    public static Enrollment createEnrollment(Student student, Course course) {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setEnrollmentsStatus(EnrollmentStatus.ENROLLED);

        return enrollment;
    }


}
