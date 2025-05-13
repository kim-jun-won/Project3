package Project3.LMS.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 과제 출제 엔티티
 *
 */
@Entity
@Getter @Setter
public class Assignment {
    @Id
    @Column(name = "assignment_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id",nullable = false)
    Professor professor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id",nullable = false)
    Course course;

    //과제 제목
    private String title;

    //과제 내용
    private String content;


    //기한, 출제일자, 업데이트 일자,
    private LocalDateTime due_date;
    private LocalDateTime created_time;
    private LocalDateTime updated_time;

    /**
     *     createAssignment() 함수 구현
     *     연관관계 주의하여 교수와 과목 Listd에 정확히 넣을것
     */

}
