package Project3.LMS.service;

import Project3.LMS.domain.Course;
import Project3.LMS.domain.Enrollment;
import Project3.LMS.domain.Syllabus;
import Project3.LMS.repostiory.CourseRepository;
import Project3.LMS.repostiory.EnrollmentRepository;
import Project3.LMS.repostiory.SyllabusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SyllabusService {
    private final SyllabusRepository syllabusRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    /**
     * 등록
     * */
    public void createSyllabus(Long courseId, String content){
        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new IllegalArgumentException("Invalid course ID"));

        Syllabus syllabus = new Syllabus();
        syllabus.setCourse(course);
        syllabus.setContent(content);

        syllabusRepository.save(syllabus);
    }

    /**
     * 수정
     * */
    public void updateSyllabusByCourseId(Long courseId, String content) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course ID"));

        Syllabus syllabus = course.getSyllabus();
        if (syllabus == null) {
            throw new IllegalStateException("해당 과목에 강의계획서가 없습니다.");
        }

        syllabus.setContent(content); // 안전하게 업데이트
    }

    /**
     * 삭제
     * */
    public void deleteSyllabus(Long syllabusId) {
        Syllabus syllabus = syllabusRepository.findById(syllabusId)
                .orElseThrow(() -> new RuntimeException("Syllabus not found"));

        Course course = syllabus.getCourse();

        if (course != null) {
            course.setSyllabus(null);   // 연결 해제
            syllabus.setCourse(null);
            courseRepository.save(course);
        }

        syllabusRepository.delete(syllabus); // 실제 삭제
    }


    /**
     * 특정과목에 대한 강의계획서 조회
     * */
    public Syllabus getSyllabusbyCourseId(Long courseId){
        return syllabusRepository.findByCourseId(courseId)
                .orElseThrow(()->new IllegalArgumentException("해당강의계획서가 존재하지 않습니다."));
    }

    /**
     * 학생이 수강중인 과목 목록 조회 (강의계획서 열람용)
     */
    public List<Course> getCoursesByStudent(Long studentId){
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);

        // enrollment list를 course list로 변환하는 코드
        return enrollments.stream()
                .map(Enrollment::getCourse)
                .collect(Collectors.toList());
    }

}
