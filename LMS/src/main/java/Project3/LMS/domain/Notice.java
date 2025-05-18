package Project3.LMS.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 공지사항
 * 교수가 과목 공지사항 작성 가능
 * Admin이 klas 전체 공지사항 작성 가능
 */
@Entity
@Getter @Setter
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id")
    private Professor professor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    /**
     * 필독 : 공지사항 작성자 타입을 정확히 명시 (교수 or 관리자)
     */
    @Enumerated(EnumType.STRING)
    private NoticeWriterType writerType;

    private String title;

    @Lob
    private String content;

    private LocalDateTime date;

    // 추가된 field
    private String fileName;
    private boolean fixed;
    private int viewCount;
}
