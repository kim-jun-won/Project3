package Project3.LMS.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 과목 엔티티
 *
 */
@Entity
@Getter @Setter
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long id;

    private String courseName;
    private int credits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id",nullable = false)
    private Professor professor;

    @OneToOne(mappedBy = "course",orphanRemoval = true, cascade = CascadeType.ALL)
    private Syllabus syllabus;

    @OneToMany(mappedBy = "course")
    private List<Timetable> timetables = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assignment> assignments = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<Notice> notices = new ArrayList<>();



    /**
     *     createCourse 함수 구현
     *     양방향 연관관계에 대해 설정
     *     ex)professor.getCourses().add(course)
     */
}
