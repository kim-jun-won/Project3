package Project3.LMS.repostiory;

import Project3.LMS.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByUid(String uid);
    public Optional<User> findByEmail(String email);

    boolean existsByEmail(String mail);

}
