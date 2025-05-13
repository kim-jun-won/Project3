package Project3.LMS.service;

import Project3.LMS.domain.*;
import Project3.LMS.repostiory.CourseRepository;
import Project3.LMS.repostiory.EnrollmentRepository;
import Project3.LMS.repostiory.StudentRepository;
import Project3.LMS.repostiory.TimetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final TimetableRepository timetableRepository;


    //==수강 신청==//
    @Transactional
    public Long enroll(Long studentId, Long courseId){

        //중복 수강신청 검사
        validateDuplicateEnrollment(studentId, courseId);

        Student student = studentRepository.findById(studentId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 학생입니다."));
        Course course = courseRepository.findOne(courseId);

        Enrollment enrollment = Enrollment.createEnrollment(student, course);

        enrollmentRepository.save(enrollment);

        return enrollment.getId();

    }

    public void validateDuplicateEnrollment(Long studentId, Long courseId){
        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);
        if (!enrollments.isEmpty()) {
            throw new IllegalStateException("이미 신청한 강의입니다.");
        }
    }

    //==수강 삭제==//
    @Transactional
    public void cancelEnroll(Long enrollmentId){
        Enrollment enrollment = enrollmentRepository.findOne(enrollmentId);
        enrollmentRepository.delete(enrollment);

    }

    // 학생 ID로 수강 신청 목록 조회
    public List<Enrollment> findEByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

}
