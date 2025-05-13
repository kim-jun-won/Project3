package Project3.LMS.repostiory;

import Project3.LMS.domain.Student;
import Project3.LMS.domain.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// JpaRepository 이용 , CRUD기능 + 도메인맞춤형 쿼리(findByStudent) 기능제공
// 더 복잡한 쿼리는 @Query이용
@Repository
public interface TimetableRepository extends JpaRepository<Timetable,Long> {
    List<Timetable> findByStudentId(Long id);

    Timetable findByStudentAndDayAndTime(Student student, String day, int time);

    /**
     * 교수 시간표 조회를 위한 query문
     */
    @Query("SELECT t FROM Timetable t WHERE t.course.professor.id = :professorId")
    List<Timetable> findByProfessorId(@Param("professorId") Long professorId);

    List<Timetable> findByCourseId(Long id);
}
