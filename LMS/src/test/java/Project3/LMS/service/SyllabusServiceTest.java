
package Project3.LMS.service;

import Project3.LMS.domain.Course;
import Project3.LMS.domain.Syllabus;
import Project3.LMS.domain.Enrollment;
import Project3.LMS.repostiory.SyllabusRepository;
import Project3.LMS.repostiory.CourseRepository;
import Project3.LMS.repostiory.EnrollmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SyllabusServiceTest {

    private SyllabusRepository syllabusRepository;
    private CourseRepository courseRepository;
    private EnrollmentRepository enrollmentRepository;
    private SyllabusService syllabusService;

    @BeforeEach
    void setUp() {
        syllabusRepository = mock(SyllabusRepository.class);
        courseRepository = mock(CourseRepository.class);
        enrollmentRepository = mock(EnrollmentRepository.class);
        syllabusService = new SyllabusService(syllabusRepository, courseRepository, enrollmentRepository);
    }

    @Test
    void createSyllabus() {
        // Given
        Long courseId = 1L;
        String content = "강의 계획 내용";
        Course course = new Course();
        course.setId(courseId);

        when(courseRepository.findById(courseId)).thenReturn(course);

        // When
        syllabusService.createSyllabus(courseId, content);

        // Then
        ArgumentCaptor<Syllabus> captor = ArgumentCaptor.forClass(Syllabus.class);
        verify(syllabusRepository).save(captor.capture());

        Syllabus saved = captor.getValue();
        assertEquals(content, saved.getContent());
        assertEquals(course, saved.getCourse());
    }

    @Test
    void updateSyllabusByCourseId() {
        // Given
        Long courseId = 1L;
        String newContent = "수정된 내용";

        Course course = new Course();
        Syllabus syllabus = new Syllabus();
        course.setSyllabus(syllabus);

        when(courseRepository.findById(courseId)).thenReturn(course);

        // When
        syllabusService.updateSyllabusByCourseId(courseId, newContent);

        // Then
        assertEquals(newContent, syllabus.getContent());
    }


    @Test
    void deleteSyllabusByCourseId() {
        // Given
        Long courseId = 1L;
        Course course = new Course();
        Syllabus syllabus = new Syllabus();

        // syllabus 필드 설정
        syllabus.setId(1L);
        syllabus.setContent("테스트 내용");

        // course에 syllabus 설정 - 이 과정에서 양방향 연관관계가 설정됨
        course.setSyllabus(syllabus);

        // mock 설정
        when(courseRepository.findById(courseId)).thenReturn(course);

        // When
        syllabusService.deleteSyllabusByCourseId(courseId);

        // Then
        verify(syllabusRepository).delete(syllabus);
        verify(courseRepository).save(course);
        assertNull(course.getSyllabus());
    }

    @Test
    void getSyllabusByCourseId() {
        // Given
        Long courseId = 1L;
        Syllabus syllabus = new Syllabus();
        syllabus.setContent("테스트 계획서");

        when(syllabusRepository.findByCourseId(courseId)).thenReturn(Optional.of(syllabus));

        // When
        Syllabus result = syllabusService.getSyllabusbyCourseId(courseId);

        // Then
        assertEquals("테스트 계획서", result.getContent());
    }

    @Test
    void getCoursesByStudent() {
        // Given
        Long studentId = 1L;
        Course course1 = new Course();
        Course course2 = new Course();

        Enrollment enrollment1 = new Enrollment();
        enrollment1.setCourse(course1);
        Enrollment enrollment2 = new Enrollment();
        enrollment2.setCourse(course2);

        List<Enrollment> enrollments = Arrays.asList(enrollment1, enrollment2);

        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(enrollments);

        // When
        List<Course> result = syllabusService.getCoursesByStudent(studentId);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains(course1));
        assertTrue(result.contains(course2));
    }
}