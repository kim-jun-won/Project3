package Project3.LMS.service;

import Project3.LMS.domain.Course;
import Project3.LMS.domain.Professor;
import Project3.LMS.domain.Student;
import Project3.LMS.domain.Timetable;
import Project3.LMS.repostiory.CourseRepository;
import Project3.LMS.repostiory.TimetableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimetableServiceTest {

    @Mock
    private TimetableRepository timetableRepository;
    @Mock
    private CourseRepository courseRepository;

    private TimetableService timetableService;

    @BeforeEach
    void setUp() {
        timetableService = new TimetableService(timetableRepository, courseRepository);
    }

    @Test
    void 학생_시간표_조회() {
        // Given
        Student student = new Student();
        student.setId(1L);

        Timetable timetable1 = new Timetable();
        timetable1.setDay("월");
        timetable1.setTime(1);

        Timetable timetable2 = new Timetable();
        timetable2.setDay("화");
        timetable2.setTime(2);

        List<Timetable> timetables = Arrays.asList(timetable1, timetable2);

        when(timetableRepository.findByStudentId(student.getId())).thenReturn(timetables);

        // When
        List<Timetable> result = timetableService.getStudentTimetable(student);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(timetable1, timetable2);
    }

    @Test
    void 시간표_추가_성공() {
        // Given
        Student student = new Student();
        student.setId(1L);
        student.setName("홍길동");

        Course course = new Course();
        course.setId(1L);
        course.setCourseName("자바프로그래밍");

        String day = "월";
        int time = 1;

        when(courseRepository.findById(1L)).thenReturn(course);
        when(timetableRepository.findByStudentAndDayAndTime(student, day, time)).thenReturn(null);

        // When
        timetableService.addTimetable(student, day, time, 1L);

        // Then
        ArgumentCaptor<Timetable> timetableCaptor = ArgumentCaptor.forClass(Timetable.class);
        verify(timetableRepository).save(timetableCaptor.capture());

        Timetable savedTimetable = timetableCaptor.getValue();
        assertThat(savedTimetable.getStudent()).isEqualTo(student);
        assertThat(savedTimetable.getCourse()).isEqualTo(course);
        assertThat(savedTimetable.getDay()).isEqualTo(day);
        assertThat(savedTimetable.getTime()).isEqualTo(time);
    }

    @Test
    void 중복된_시간_예외발생() {
        // Given
        Student student = new Student();
        student.setId(1L);

        String day = "월";
        int time = 1;

        Timetable existingTimetable = new Timetable();
        when(timetableRepository.findByStudentAndDayAndTime(student, day, time))
                .thenReturn(existingTimetable);

        // When & Then
        assertThatThrownBy(() ->
                timetableService.addTimetable(student, day, time, 1L)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 해당 시간에 과목이 등록되어 있습니다");
    }

    @Test
    void 교수_시간표_조회() {
        // Given
        Long professorId = 1L;
        List<Timetable> timetables = Arrays.asList(
                new Timetable(),
                new Timetable()
        );

        when(timetableRepository.findByProfessorId(professorId)).thenReturn(timetables);

        // When
        List<Timetable> result = timetableService.getProfessorTimetable(professorId);

        // Then
        assertThat(result).hasSize(2);
        verify(timetableRepository).findByProfessorId(professorId);
    }

    @Test
    void 시간표_삭제() {
        // Given
        Student student = new Student();
        String day = "월";
        int time = 1;
        Timetable timetable = new Timetable();

        when(timetableRepository.findByStudentAndDayAndTime(student, day, time))
                .thenReturn(timetable);

        // When
        timetableService.deleteByStudentAndDayAndTime(student, day, time);

        // Then
        verify(timetableRepository).delete(timetable);
    }

    @Test
    void 과목별_수강학생_조회() {
        // Given
        Course course = new Course();
        course.setId(1L);

        List<Timetable> timetables = Arrays.asList(
                createTimetableWithStudent(1L),
                createTimetableWithStudent(2L)
        );

        when(timetableRepository.findByCourseId(course.getId())).thenReturn(timetables);

        // When
        List<Student> result = timetableService.getStudentsByCourse(course);

        // Then
        assertThat(result).hasSize(2);
        verify(timetableRepository).findByCourseId(course.getId());
    }

    private Timetable createTimetableWithStudent(Long studentId) {
        Timetable timetable = new Timetable();
        Student student = new Student();
        student.setId(studentId);
        timetable.setStudent(student);
        return timetable;
    }
}