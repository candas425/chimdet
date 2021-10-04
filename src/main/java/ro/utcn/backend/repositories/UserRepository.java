package ro.utcn.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.utcn.backend.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsernameAndParola(String username, String parola);
}
