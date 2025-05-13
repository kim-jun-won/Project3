package Project3.LMS.service;

import Project3.LMS.domain.Course;
import Project3.LMS.domain.Student;
import Project3.LMS.domain.Timetable;
import Project3.LMS.repostiory.CourseRepository;
import Project3.LMS.repostiory.CourseSearch;
import Project3.LMS.repostiory.TimetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TimetableService {
    private final TimetableRepository timetableRepository;
    private final CourseRepository courseRepository;

    /**
     * student 에 따른 Timetable 전달해줌
     * */
    public List<Timetable> getStudentTimetable(Student student)
    {
        return timetableRepository.findByStudentId(student.getId());
    }

    /**
     * Timetable add를 위해 모든 과목 조회
     * */
    public List<Course> getAllCourses() {
        return courseRepository.findAll(new CourseSearch());
    }

    /***
     * Timetable 추가
     */
    public void addTimetable(Student student, String day, int time, Long courseId)
    {
        // 과목조회
        Course course = courseRepository.findById(courseId);

        // 중복 시간표 조회 방지
        Timetable existing = timetableRepository.findByStudentAndDayAndTime(student,day,time);
        if(existing != null){
            throw new IllegalArgumentException("이미 해당 시간에 과목이 등록되어 있습니다.");
        }

        // 시간표 생성 및 저장
        Timetable timetable = new Timetable();
        timetable.setStudent(student);
        timetable.setCourse(course);
        timetable.setDay(day);
        timetable.setTime(time);

        timetableRepository.save(timetable);
    }


    /**
     * 시간표 삭제
     */
    public void deleteByStudentAndDayAndTime(Student student, String day, int time) {
        Timetable timetable = timetableRepository.findByStudentAndDayAndTime(student, day, time);
        if (timetable != null) {
            timetableRepository.delete(timetable);
        }
    }

    /**
     * 과목을 전달하면 해당 과목을 수강하는 학생을 뽑는 함수
     */
    public List<Student> getStudentsByCourse(Course course) {
        List<Timetable> timetables = timetableRepository.findByCourseId(course.getId());
        return timetables.stream()
                .map(Timetable::getStudent)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 교수에 따른 Timetable 전달해줌
     */
    public List<Timetable> getProfessorTimetable(Long professorId) {
        return timetableRepository.findByProfessorId(professorId);
    }


}
