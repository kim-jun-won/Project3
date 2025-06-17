package Project3.LMS.service;

import Project3.LMS.domain.Course;
import Project3.LMS.domain.Enrollment;
import Project3.LMS.domain.EnrollmentStatus;
import Project3.LMS.domain.Student;
import Project3.LMS.repostiory.CourseRepository;
import Project3.LMS.repostiory.EnrollmentRepository;
import Project3.LMS.repostiory.StudentRepository;
import Project3.LMS.repostiory.TimetableRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final TimetableRepository timetableRepository;

    @Transactional
    public Long enroll(Long studentId, Long courseId) {
        validateDuplicateEnrollment(studentId, courseId);
        validateDuplicateTime(studentId, courseId);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 학생입니다."));
        Course course = courseRepository.findOne(courseId);

        if (course.getEnrolledCount() >= course.getCapacity()) {
            throw new IllegalStateException("수강 정원이 초과되어 신청할 수 없습니다.");
        }

        Enrollment enrollment = Enrollment.createEnrollment(student, course);
        course.incrementEnrollment(); // 현재 인원 수 증가

        // courseRepository.save(course); -> 이거하니까 transactional 문제가 발생
        enrollmentRepository.save(enrollment);

        return enrollment.getId();
    }

    public void validateDuplicateEnrollment(Long studentId, Long courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);
        if (!enrollments.isEmpty()) {
            throw new IllegalStateException("이미 신청한 강의입니다.");
        }
    }

    public boolean isAlreadyEnrolled(Long studentId, Long courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);
        return !enrollments.isEmpty();
    }

    public void validateDuplicateTime(Long studentId, Long courseId) {
        Course newCourse = courseRepository.findOne(courseId);
        String newDay = newCourse.getDay();
        int newTime = newCourse.getTime();

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        for (Enrollment e : enrollments) {
            Course existingCourse = e.getCourse();
            if (existingCourse.getDay().equals(newDay) && existingCourse.getTime() == newTime) {
                throw new IllegalStateException("이미 동일한 시간에 수강 중인 강의가 있습니다.");
            }
        }
    }

    @Transactional
    public void cancelEnroll(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findOne(enrollmentId);
        enrollment.setEnrollmentsStatus(EnrollmentStatus.DROPPED);
        Course course = enrollment.getCourse();
        course.decrementEnrollment(); // 수강 인원 감소
        courseRepository.save(course);
        enrollmentRepository.delete(enrollment);
    }

    public List<Enrollment> findEByStudent(Long studentId) {
        return enrollmentRepository.findWithCourseAndProfessorByStudentId(studentId);
    }

    @Transactional
    public void assignGrade(Enrollment enrollment, String grade) {
        enrollment.setGrade(grade);
        enrollmentRepository.save(enrollment);
    }
}
