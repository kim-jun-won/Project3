package Project3.LMS.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 해당 엔티티는 과제 제출 파일이 여러개 일때를 대비해 설계
 * 만약 과제 제출 파일이 여러개라면 해당 엔티티 사용
 */
@Entity
@Getter @Setter
public class AssignmentFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_file_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id",nullable = false)
    private Assignment assignment;

    private String file_path;
    private String file_name;
    private String file_type;

    private LocalDateTime updated_time;
}
