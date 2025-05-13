package Project3.LMS.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class OnlineLecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String url;

    private LocalDateTime deadline;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;


    public static OnlineLecture createOnlineLecture(String title, String url, LocalDateTime deadline, Course course) {
        OnlineLecture onlineLecture = new OnlineLecture();
        onlineLecture.setTitle(title);
        onlineLecture.setUrl(url);
        onlineLecture.setDeadline(deadline);
        onlineLecture.setCourse(course);

        return onlineLecture;
    }

}
