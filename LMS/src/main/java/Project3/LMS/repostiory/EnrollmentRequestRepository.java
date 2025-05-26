package Project3.LMS.repostiory;

import Project3.LMS.domain.EnrollmentRequest;
import Project3.LMS.domain.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRequestRepository extends JpaRepository<EnrollmentRequest, Long> {

    /**
     * 특정 학생이 요청한 모든 요청
     */
    List<EnrollmentRequest> findByStudentId(Long studentId);

    /**
     * 특정 과목에 대한 요청 전체 조회 (교수 전용)
     * */
    List<EnrollmentRequest> findByCourseId(Long courseId);

    /**
     * 교수의 모든 과목에 대한 요청 조회 (교수 ID로)
     * */
    List<EnrollmentRequest> findByCourseProfessorId(Long professorId);


    /**
     * 특정 과목 + 대기 상태 요청만 조회 (교수 승인 리스트 필터용)
     * */
    List<EnrollmentRequest> findByCourseIdAndStatus(Long courseId, RequestStatus status);
}
