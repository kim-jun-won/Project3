package Project3.LMS.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 강의계획서 엔티티
 */
@Entity
@Getter @Setter
public class Syllabus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "syllabus_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "course_id")
    private Course course;

    private String content;

    /**
     * 연관관계 편의 메서드
     * */
    public void setCourse(Course course) {
        // 양방향 연관관계 설정을 수동으로 처리
        this.course = course;
        if (course != null && course.getSyllabus() != this) {
            course.setSyllabus(this);
        }
    }
}
