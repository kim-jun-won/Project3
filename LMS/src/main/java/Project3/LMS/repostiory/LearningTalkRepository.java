package Project3.LMS.repostiory;

import Project3.LMS.domain.Course;
import Project3.LMS.domain.LearningTalk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningTalkRepository extends JpaRepository<LearningTalk, Long> {
    List<LearningTalk> findByCourseOrderByCreatedAtAsc(Course course); // 과목별 정렬된 글 목록
}