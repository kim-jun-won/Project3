package Project3.LMS.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class LectureMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_id")
    private Long id;

    private String title;

    private String filePath; // 파일 경로 또는 URL

    private String writer; // 작성자 이름

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate; // 작성일

    private int viewCount; // 조회수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // 연관관계 메서드
    public void setCourse(Course course) {
        this.course = course;
        course.getLectureMaterials().add(this);
    }

    // 생성 메서드
    public static LectureMaterial createMaterial(String title, String filePath, Course course, String writer) {
        LectureMaterial material = new LectureMaterial();
        material.setTitle(title);
        material.setFilePath(filePath);
        material.setCourse(course);
        material.setWriter(writer);
        material.setCreatedDate(LocalDateTime.now());
        material.setViewCount(0); // 등록 시 조회수 0
        return material;
    }

    // 조회수 증가 메서드
    public void incrementViewCount() {
        this.viewCount++;
    }
}
