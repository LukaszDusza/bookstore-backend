package devlab.app.auth.User;

import devlab.app.model.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserApp, Long> {

    UserApp findByUsername(String username);

}
