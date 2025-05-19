package Project3.LMS.repostiory;

import Project3.LMS.domain.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    public Optional<Professor> findByPid(String pid);

    // ProfessorRepository.java
    @Query("SELECT p FROM Professor p JOIN FETCH p.user WHERE p.pid = :pid")
    Optional<Professor> findByPidWithUser(@Param("pid") String pid);
}
