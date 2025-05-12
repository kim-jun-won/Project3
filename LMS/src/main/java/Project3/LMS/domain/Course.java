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

<<<<<<< HEAD
    @OneToOne(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
=======
    @OneToOne(mappedBy = "course",orphanRemoval = true, cascade = CascadeType.ALL)
>>>>>>> junwon
    private Syllabus syllabus;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Timetable> timetables = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notice> notices = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assignment> assignments = new ArrayList<>();

    /**
     *     연관관계 메소드
     */
    public void setProfessor(Professor professor) {
        this.professor = professor;
        professor.getCourses().add(this);
    }

    public void setSyllabus(Syllabus syllabus) {
        this.syllabus = syllabus;
        syllabus.setCourse(this);
    }

    public void addTimetable(Timetable timetable) {
        timetables.add(timetable);
        timetable.setCourse(this);
    }

    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
        enrollment.setCourse(this);
    }

    public void addAssignment(Assignment assignment) {
        assignments.add(assignment);
        assignment.setCourse(this);
    }

    public void addNotice(Notice notice) {
        notices.add(notice);
        notice.setCourse(this);
    }


    /**
     *     생성 메소드
     */

<<<<<<< HEAD
    public static Course createCourse(String courseName, int credits, Professor professor) {
        Course course = new Course();
        course.setCourseName(courseName);
        course.setCredits(credits);
        course.setProfessor(professor);

        return course;

    }


}
=======
}
>>>>>>> junwon
