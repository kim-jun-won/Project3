package Project3.LMS.service;

import Project3.LMS.domain.*;
import Project3.LMS.domain.RequestStatus;
import Project3.LMS.repostiory.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.*;

import static Project3.LMS.domain.RequestStatus.WAITING;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnrollmentRequestServiceTest {

    private EnrollmentRequestRepository requestRepository = mock(EnrollmentRequestRepository.class);
    private EnrollmentRepository enrollmentRepository = mock(EnrollmentRepository.class);
    private StudentRepository studentRepository = mock(StudentRepository.class);
    private CourseRepository courseRepository = mock(CourseRepository.class);

    private EnrollmentRequestService service;

    @BeforeEach
    void setUp() {
        service = new EnrollmentRequestService(
                requestRepository,
                enrollmentRepository,
                studentRepository,
                courseRepository
        );
    }

    @Test
    void 수강승인요청_정상처리() {
        Student student = new Student();
        student.setId(1L);

        Course course = new Course();
        course.setId(10L);
        course.setCapacity(2);
        course.setEnrolledCount(1);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(eq(10L))).thenReturn(course);
        when(requestRepository.findByStudentId(1L)).thenReturn(Collections.emptyList());

        service.sendRequest(1L, 10L, "듣고 싶어요");

        ArgumentCaptor<EnrollmentRequest> captor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(requestRepository).save(captor.capture());

        EnrollmentRequest saved = captor.getValue();
        assertThat(saved.getCourse()).isEqualTo(course);
        assertThat(saved.getStudent()).isEqualTo(student);
        assertThat(saved.getStatus()).isEqualTo(WAITING);
        assertThat(saved.getReason()).isEqualTo("듣고 싶어요");
    }

    @Test
    void 중복요청이면_예외() {
        Student student = new Student();
        student.setId(1L);

        Course course = new Course();
        course.setId(10L);

        EnrollmentRequest existing = new EnrollmentRequest();
        existing.setStudent(student);
        existing.setCourse(course);
        existing.setStatus(WAITING);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(eq(10L))).thenReturn(course);
        when(requestRepository.findByStudentId(1L)).thenReturn(List.of(existing));

        assertThatThrownBy(() ->
                service.sendRequest(1L, 10L, "듣고싶어요")
        ).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 수강 승인 요청");
    }

    @Test
    void 승인시_enrollment생성되고_수강인원_증가() {
        Student student = new Student();
        student.setId(1L);

        Course course = new Course();
        course.setId(10L);
        course.setCapacity(3);
        course.setEnrolledCount(1);

        EnrollmentRequest request = new EnrollmentRequest();
        request.setId(99L);
        request.setStatus(WAITING);
        request.setCourse(course);
        request.setStudent(student);

        when(requestRepository.findById(99L)).thenReturn(Optional.of(request));

        service.approveRequest(99L);

        verify(enrollmentRepository).save(any(Enrollment.class));
        assertThat(request.getStatus()).isEqualTo(Project3.LMS.domain.RequestStatus.APPROVED);
        assertThat(course.getEnrolledCount()).isEqualTo(2);
    }

    @Test
    void 정원이_꽉차있으면_승인불가() {
        Student student = new Student();
        Course course = new Course();
        course.setCapacity(1);
        course.setEnrolledCount(1); // 꽉참

        EnrollmentRequest request = new EnrollmentRequest();
        request.setStatus(WAITING);
        request.setCourse(course);
        request.setStudent(student);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        assertThatThrownBy(() ->
                service.approveRequest(1L)
        ).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("정원이 초과");
    }
}
