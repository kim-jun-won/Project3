package Project3.LMS.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 기능:
 * 수강신청 list에서 course를 가져와 메인 화면에 시간표 출력
 */
@Entity
@Getter @Setter
public class Student {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long id;

    private String sid; //학생 학번
    private String name;
    private String email;
    private String department;
    private String password;
    private String phoneNumber;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "student")
    private List<Timetable> timetables= new ArrayList<>();

    @OneToMany(mappedBy = "student")
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    private List<AssignmentSubmission> assignmentSubmissions = new ArrayList<>();
}
