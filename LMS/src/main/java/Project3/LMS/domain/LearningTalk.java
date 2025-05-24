package Project3.LMS.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class LearningTalk {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "learningTalk_id")
    private Long id;

    private String title;

    @Lob
    private String content;

    private LocalDateTime createdAt;

    @ManyToOne
    private Course course;

    // 작성자 구분: 교수 또는 학생 (둘 중 하나만 존재)
    @ManyToOne
    private Professor professor;

    @ManyToOne
    private Student student;

    //학생 익명 고유 번호
    private String anonymousName;

    public boolean isProfessor() {
        return professor != null;
    }
}
