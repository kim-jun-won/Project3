package Project3.LMS.repostiory;

import Project3.LMS.domain.Course;
import Project3.LMS.domain.Notice;
import Project3.LMS.domain.NoticeWriterType;
import Project3.LMS.domain.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    /**
     * 학생 : 수강중인 과목들의 공지사항 조회
     * */
    List<Notice> findByCourseIn(List<Course> courses);

    /**
     * 교수 : 본인이 담당한 과목들의 공지사항 조회
     * */
    List<Notice> findByCourseProfessor(Professor professor);

    /***
     * 관리자 : 시스템 공지사항
     */
    List<Notice> findByWriterType(NoticeWriterType noticeWriterType);

    /**
     * 홈화면에서의 학생이 수강중인 과목의 공지사항 찾기 위해
     *  */
    List<Notice> findByCourse(Course course);
}
