package devlab.app.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/")
public class LoginController {



//    @PostMapping("sign")
//    public ResponseEntity<?> login(@RequestParam(value = "login") String login, @RequestParam(value = "password") String password) {
//        System.out.println(login);
//        System.out.println(password);
//       return new ResponseEntity<>(HttpStatus.OK);
//
//    }

//    @RequestMapping(value = "/login", method = RequestMethod.GET)
//    public String echo( @AuthenticationPrincipal final UserDetails user) {
//        return "Hello " + user.getUsername() + ",  " + user.getAuthorities().iterator().next();
//    }

}
