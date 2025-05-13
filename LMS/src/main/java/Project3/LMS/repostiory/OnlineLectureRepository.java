package Project3.LMS.repostiory;

import Project3.LMS.domain.OnlineLecture;
import Project3.LMS.domain.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OnlineLectureRepository extends JpaRepository<OnlineLecture, Long> {

}
