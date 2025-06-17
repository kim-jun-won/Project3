package Project3.LMS.service;

import Project3.LMS.domain.Course;
import Project3.LMS.domain.OnlineLecture;
import Project3.LMS.dto.OnlineLectureDTO;
import Project3.LMS.repostiory.CourseRepository;
import Project3.LMS.repostiory.OnlineLectureRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class OnlineLectureServiceTest {

    @Test
    void 온라인강의_등록_성공() {
        // given
        Course course = new Course();
        course.setId(1L);

        CourseRepository courseRepo = mock(CourseRepository.class);
        OnlineLectureRepository lectureRepo = mock(OnlineLectureRepository.class);
        OnlineLectureService service = new OnlineLectureService(lectureRepo, courseRepo);

        OnlineLectureDTO dto = new OnlineLectureDTO();
        dto.setTitle("1주차 강의");
        dto.setUrl("http://example.com/lecture/1");
        dto.setDeadline(LocalDateTime.now().plusDays(3));

        when(courseRepo.findOne(1L)).thenReturn(course);

        // when
        Long result = service.addOnlineLecture(dto, 1L);

        // then
        assertThat(result).isNotNull();
        verify(lectureRepo).save(any(OnlineLecture.class));
    }

    @Test
    void 온라인강의_등록_실패_없는_과목() {
        // given
        CourseRepository courseRepo = mock(CourseRepository.class);
        OnlineLectureRepository lectureRepo = mock(OnlineLectureRepository.class);
        OnlineLectureService service = new OnlineLectureService(lectureRepo, courseRepo);

        OnlineLectureDTO dto = new OnlineLectureDTO();
        dto.setTitle("강의X");
        dto.setUrl("http://none.com");
        dto.setDeadline(LocalDateTime.now().plusDays(5));

        when(courseRepo.findOne(999L)).thenReturn(null);

        // when & then
        try {
            service.addOnlineLecture(dto, 999L);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("존재하지 않는 강의입니다.");
        }

        verify(lectureRepo, never()).save(any());
    }
}
