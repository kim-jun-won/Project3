package Project3.LMS.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 과제 제출 엔티티
 * 현재 세션에 있는 학생 id를 가져와 제출 구현.
 */
@Entity
@Getter @Setter
public class AssignmentSubmission {
    @Id
    @Column(name = "submission_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "assignment_id",nullable = false)
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "student_id",nullable = false)
    private Student student;

    private String content;
    private String title;

    /// ////////////////////////////////
    private String file_path;
    private String file_name;
    private String file_type;
    /// ///////////////////////////////


    private int grade;
    private String feedback;

    private LocalDateTime submitted_time;

    //createAssignmentSubmission 함수 구현
}
