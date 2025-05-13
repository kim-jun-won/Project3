package Project3.LMS.repostiory;

import Project3.LMS.domain.Student;
import Project3.LMS.domain.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// JpaRepository 이용 , CRUD기능 + 도메인맞춤형 쿼리(findByStudent) 기능제공
// 더 복잡한 쿼리는 @Query이용
@Repository
public interface TimetableRepository extends JpaRepository<Timetable,Long> {
    List<Timetable> findByStudentId(Long id);

    Timetable findByStudentAndDayAndTime(Student student, String day, int time);

    List<Timetable> findByCourseId(Long courseId);
}
