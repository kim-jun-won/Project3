package Project3.LMS.service;

import Project3.LMS.domain.Course;
import Project3.LMS.domain.Professor;
import Project3.LMS.repostiory.CourseRepository;
import Project3.LMS.repostiory.EnrollmentRepository;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CourseServiceTest {

    @Test
    void 강의등록_성공() {
        // given
        CourseRepository courseRepo = mock(CourseRepository.class);
        EnrollmentRepository enrollmentRepo = mock(EnrollmentRepository.class);
        CourseService courseService = new CourseService(courseRepo, enrollmentRepo);

        Professor professor = new Professor();
        professor.setName("홍교수");

        Course course = new Course();
        course.setId(1L);
        course.setCourseName("자료구조");
        course.setCredits(3);
        course.setProfessor(professor);

        when(courseRepo.findByName("자료구조")).thenReturn(Collections.emptyList());

        // when
        Long result = courseService.registerCourse(course);

        // then
        assertThat(result).isEqualTo(1L);
        verify(courseRepo).save(course);
    }

    @Test
    void 강의등록_중복_예외() {
        // given
        CourseRepository courseRepo = mock(CourseRepository.class);
        EnrollmentRepository enrollmentRepo = mock(EnrollmentRepository.class);
        CourseService courseService = new CourseService(courseRepo, enrollmentRepo);

        Course existing = new Course();
        existing.setCourseName("자료구조");

        Course newCourse = new Course();
        newCourse.setCourseName("자료구조");

        when(courseRepo.findByName("자료구조")).thenReturn(List.of(existing));

        // when & then
        try {
            courseService.registerCourse(newCourse);
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).contains("이미 존재하는 강의입니다.");
        }

        verify(courseRepo, never()).save(any());
    }

    @Test
    void 강의삭제_정상작동() {
        // given
        CourseRepository courseRepo = mock(CourseRepository.class);
        EnrollmentRepository enrollmentRepo = mock(EnrollmentRepository.class);
        CourseService courseService = new CourseService(courseRepo, enrollmentRepo);

        Course course = new Course();
        course.setId(2L);
        when(courseRepo.findOne(2L)).thenReturn(course);

        // when
        courseService.deleteCourse(2L);

        // then
        verify(courseRepo).delete(course);
    }
}
