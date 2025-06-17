package Project3.LMS.service;

import Project3.LMS.domain.Course;
import Project3.LMS.domain.Enrollment;
import Project3.LMS.domain.Student;
import Project3.LMS.domain.EnrollmentStatus;
import Project3.LMS.repostiory.CourseRepository;
import Project3.LMS.repostiory.EnrollmentRepository;
import Project3.LMS.repostiory.StudentRepository;
import Project3.LMS.repostiory.TimetableRepository;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class EnrollmentsServiceTest {

    @Test
    void 수강신청_성공() {
        // given
        Student student = new Student();
        student.setId(1L);
        Course course = new Course();
        course.setId(10L);
        course.setCapacity(10);
        course.setEnrolledCount(0);

        StudentRepository studentRepo = mock(StudentRepository.class);
        CourseRepository courseRepo = mock(CourseRepository.class);
        EnrollmentRepository enrollmentRepo = mock(EnrollmentRepository.class);
        TimetableRepository timetableRepo = mock(TimetableRepository.class);

        EnrollmentService service = new EnrollmentService(
                enrollmentRepo, studentRepo, courseRepo, timetableRepo
        );

        when(studentRepo.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepo.findOne(10L)).thenReturn(course);
        when(enrollmentRepo.findByStudentIdAndCourseId(1L, 10L)).thenReturn(Collections.emptyList());
        when(enrollmentRepo.findByStudentId(1L)).thenReturn(Collections.emptyList());

        // when
        Long enrollId = service.enroll(1L, 10L);

        // then
        assertThat(enrollId).isNotNull();
        verify(enrollmentRepo).save(any(Enrollment.class));
    }

    @Test
    void 수강신청_중복시_예외발생() {
        // given
        Enrollment enrollment = new Enrollment();

        StudentRepository studentRepo = mock(StudentRepository.class);
        CourseRepository courseRepo = mock(CourseRepository.class);
        EnrollmentRepository enrollmentRepo = mock(EnrollmentRepository.class);
        TimetableRepository timetableRepo = mock(TimetableRepository.class);

        EnrollmentService service = new EnrollmentService(
                enrollmentRepo, studentRepo, courseRepo, timetableRepo
        );

        when(enrollmentRepo.findByStudentIdAndCourseId(1L, 1L)).thenReturn(List.of(enrollment));

        // when & then
        try {
            service.enroll(1L, 1L);
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).contains("이미 신청한 강의입니다.");
        }

        verify(enrollmentRepo, never()).save(any());
    }

    @Test
    void 수강취소_정상작동() {
        // given
        Course course = new Course();
        course.setEnrolledCount(3);

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);

        EnrollmentRepository enrollmentRepo = mock(EnrollmentRepository.class);
        StudentRepository studentRepo = mock(StudentRepository.class);
        CourseRepository courseRepo = mock(CourseRepository.class);
        TimetableRepository timetableRepo = mock(TimetableRepository.class);

        EnrollmentService service = new EnrollmentService(
                enrollmentRepo, studentRepo, courseRepo, timetableRepo
        );

        when(enrollmentRepo.findOne(100L)).thenReturn(enrollment);

        // when
        service.cancelEnroll(100L);

        // then
        verify(enrollmentRepo).delete(enrollment);
        verify(courseRepo).save(course);
        assertThat(course.getEnrolledCount()).isEqualTo(2);
    }
}
