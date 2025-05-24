package Project3.LMS.service;

import Project3.LMS.domain.Course;
import Project3.LMS.domain.LearningTalk;
import Project3.LMS.domain.Student;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LearningTalkServiceTest {

    @Test
    void 학습톡톡_객체_저장_기본값_확인() {
        // Given
        Course course = new Course();
        course.setId(1L);

        Student student = new Student();
        student.setId(1L);

        LearningTalk talk = new LearningTalk();
        talk.setCourse(course);
        talk.setStudent(student);
        talk.setContent("질문이 있습니다.");
        talk.setCreatedAt(LocalDateTime.now());
        talk.setAnonymousName("학생#1234");

        // When + Then
        assertThat(talk.getCourse().getId()).isEqualTo(1L);
        assertThat(talk.getStudent().getId()).isEqualTo(1L);
        assertThat(talk.getContent()).isEqualTo("질문이 있습니다.");
        assertThat(talk.getAnonymousName()).isEqualTo("학생#1234");
        assertThat(talk.getCreatedAt()).isNotNull();
    }
}
