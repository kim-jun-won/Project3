package Project3.LMS.repostiory;

import Project3.LMS.domain.LectureMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LectureMaterialRepository extends JpaRepository<LectureMaterial, Long> {
    List<LectureMaterial> findByCourseId(Long courseId);

}