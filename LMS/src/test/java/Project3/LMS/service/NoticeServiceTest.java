package Project3.LMS.service;

import Project3.LMS.domain.*;
import Project3.LMS.repostiory.CourseRepository;
import Project3.LMS.repostiory.EnrollmentRepository;
import Project3.LMS.repostiory.NoticeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @Mock
    private NoticeRepository noticeRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private EnrollmentRepository enrollmentRepository;

    private NoticeService noticeService;

    @BeforeEach
    void setUp() {
        noticeService = new NoticeService(noticeRepository, courseRepository, enrollmentRepository);
    }

    @Test
    void 교수_공지사항_등록() {
        // Given
        Long courseId = 1L;
        Professor professor = new Professor();
        professor.setId(1L);
        professor.setName("김교수");

        Course course = new Course();
        course.setId(courseId);
        course.setProfessor(professor);

        String title = "시험 공지";
        String content = "중간고사 일정 안내";
        boolean fixed = true;
        String fileName = "exam_schedule.pdf";

        when(courseRepository.findById(courseId)).thenReturn(course);

        // When
        noticeService.createByProfessor(courseId, professor, title, content, fixed, fileName);

        // Then
        ArgumentCaptor<Notice> noticeCaptor = ArgumentCaptor.forClass(Notice.class);
        verify(noticeRepository).save(noticeCaptor.capture());
        Notice savedNotice = noticeCaptor.getValue();

        assertThat(savedNotice.getTitle()).isEqualTo(title);
        assertThat(savedNotice.getContent()).isEqualTo(content);
        assertThat(savedNotice.isFixed()).isEqualTo(fixed);
        assertThat(savedNotice.getFileName()).isEqualTo(fileName);
        assertThat(savedNotice.getWriterType()).isEqualTo(NoticeWriterType.PROFESSOR);
    }

    @Test
    void 교수_공지사항_수정() {
        // Given
        Long noticeId = 1L;
        Professor professor = new Professor();
        professor.setId(1L);

        Notice notice = new Notice();
        notice.setId(noticeId);
        notice.setProfessor(professor);
        notice.setTitle("원래 제목");
        notice.setContent("원래 내용");

        when(noticeRepository.findById(noticeId)).thenReturn(Optional.of(notice));

        // When
        String newTitle = "수정된 제목";
        String newContent = "수정된 내용";
        noticeService.updateNotice(noticeId, professor, newTitle, newContent, true, "new_file.pdf");

        // Then
        assertThat(notice.getTitle()).isEqualTo(newTitle);
        assertThat(notice.getContent()).isEqualTo(newContent);
    }

    @Test
    void 다른교수_공지사항_수정시_예외발생() {
        // Given
        Long noticeId = 1L;
        Professor originalProfessor = new Professor();
        originalProfessor.setId(1L);

        Professor differentProfessor = new Professor();
        differentProfessor.setId(2L);

        Notice notice = new Notice();
        notice.setId(noticeId);
        notice.setProfessor(originalProfessor);

        when(noticeRepository.findById(noticeId)).thenReturn(Optional.of(notice));

        // When & Then
        assertThatThrownBy(() ->
                noticeService.updateNotice(noticeId, differentProfessor, "새 제목", "새 내용", true, null)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("본인이 작성한 공지만 수정할 수 있습니다");
    }

    @Test
    void 교수_공지사항_삭제() {
        // Given
        Long noticeId = 1L;
        Professor professor = new Professor();
        professor.setId(1L);

        Notice notice = new Notice();
        notice.setId(noticeId);
        notice.setProfessor(professor);

        when(noticeRepository.findById(noticeId)).thenReturn(Optional.of(notice));

        // When
        noticeService.deleteNotice(noticeId, professor);

        // Then
        verify(noticeRepository).delete(notice);
    }

    @Test
    void 학생_수강과목_공지사항_조회() {
        // Given
        Student student = new Student();
        student.setId(1L);

        Course course1 = new Course();
        Course course2 = new Course();

        Enrollment enrollment1 = new Enrollment();
        enrollment1.setCourse(course1);
        Enrollment enrollment2 = new Enrollment();
        enrollment2.setCourse(course2);

        List<Enrollment> enrollments = Arrays.asList(enrollment1, enrollment2);
        List<Notice> expectedNotices = Arrays.asList(new Notice(), new Notice());

        when(enrollmentRepository.findByStudentId(student.getId())).thenReturn(enrollments);
        when(noticeRepository.findByCourseIn(Arrays.asList(course1, course2))).thenReturn(expectedNotices);

        // When
        List<Notice> actualNotices = noticeService.findStudentNotices(student);

        // Then
        assertThat(actualNotices).isEqualTo(expectedNotices);
    }

    @Test
    void 조회수_증가() {
        // Given
        Notice notice = new Notice();
        notice.setViewCount(0);

        // When
        noticeService.incrementViewCount(notice);

        // Then
        assertThat(notice.getViewCount()).isEqualTo(1);
    }
}