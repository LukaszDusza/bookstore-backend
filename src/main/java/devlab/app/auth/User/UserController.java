package devlab.app.auth.User;


import devlab.app.model.UserApp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/")
public class UserController {

    private UserRepository userRepository;
    private PasswordEncoder bCryptPasswordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("login")
    public void  echo(@AuthenticationPrincipal final UserDetails user) {
    }


    @PostMapping("sign")
    public ResponseEntity<String> signUp (@RequestBody UserApp userApp) {
        UserApp user = userRepository.findByUsername(userApp.getUsername());
        if (user == null) {
            userApp.setPassword(bCryptPasswordEncoder.encode(userApp.getPassword()));
            userRepository.save(userApp);
            return new ResponseEntity<>(userApp.getUsername(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Username: " + user.getUsername() + " already exist!",HttpStatus.CONFLICT);
        }
    }
}
