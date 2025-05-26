package Project3.LMS.repostiory;

import Project3.LMS.domain.Student;
import Project3.LMS.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    //학번 찾는 로직
    public Optional<Student> findBySid(String sid);

    // StudentRepository.java
    @Query("SELECT s FROM Student s JOIN FETCH s.user WHERE s.sid = :sid")
    Optional<Student> findBySidWithUser(@Param("sid") String sid);
}
