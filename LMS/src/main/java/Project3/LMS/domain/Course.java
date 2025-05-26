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

    private int capacity;       // 수강 정원
    private int enrolledCount;  // 현재 수강 인원 (관리용)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id",nullable = false)
    private Professor professor;

    @OneToOne(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Syllabus syllabus;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Timetable> timetables = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notice> notices = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assignment> assignments = new ArrayList<>();


    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List <OnlineLecture> onlineLectures = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LectureMaterial> lectureMaterials = new ArrayList<>();

    private String day;
    private int time;

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

    public void addOnlineLecture(OnlineLecture onlineLecture) {
        onlineLectures.add(onlineLecture);
        onlineLecture.setCourse(this);

    }

    public void addLectureMaterial(LectureMaterial material) {
        lectureMaterials.add(material);
        material.setCourse(this);
    }

    // 수강 정원 증가 메소드
    public void incrementEnrollment() {
        if (enrolledCount >= capacity) {
            throw new IllegalStateException("수강 정원이 초과되었습니다.");
        }
        this.enrolledCount += 1;
    }

    //수강 정원 감소 메소드 (삭제 시 등)
    public void decrementEnrollment() {
        if (enrolledCount > 0) {
            this.enrolledCount -= 1;
        }
    }

    // 생성 메소드
    public static Course createCourse(String courseName, int credits, Professor professor, String day, int time, int capacity) {
        Course course = new Course();
        course.setCourseName(courseName);
        course.setCredits(credits);
        course.setProfessor(professor);
        course.setDay(day);
        course.setTime(time);
        course.setCapacity(capacity);
        course.setEnrolledCount(0); // 초기값 설정
        return course;
    }

    /**
     *     생성 메소드
     */
    public static Course createCourse(String courseName, int credits, Professor professor, String day, int time) {
        Course course = new Course();
        course.setCourseName(courseName);
        course.setCredits(credits);
        course.setProfessor(professor);

        course.setDay(day);
        course.setTime(time);

        return course;

    }
}

