package devlab.app.auth.security;



import devlab.app.auth.User.UserServiceImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static devlab.app.auth.security.Constans.INDEX;
import static devlab.app.auth.security.Constans.SIGN_UP_URL;
import static devlab.app.auth.security.Constans.SWAGGER;


@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {


    private UserServiceImpl userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    public WebSecurity(UserServiceImpl userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers(INDEX, SWAGGER, SIGN_UP_URL).permitAll()
                .anyRequest()
                .authenticated()
                .and()
                //kazde zapytanie do sciezki zabezpieczonej przechodzi przez filtry
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                //zarzadzanie sesjami
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // ALWAYS - sesja zostanie utworzona, jelsi jeszcze nie zostala utworzona.
        // ifRequired - sesja zostanie utworzona, w razie potrzeby (default)
        // NEVER - spring nigdy nie utorzy sesji, ale uzyje aktualnej sesji jeśli istnieje.
        // STATELESS - spring nie utworzy ani nie uzyj ezadnej sesji.


    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
       auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }


}
