package devlab.app.auth.security;


import devlab.app.auth.User.UserServiceImpl;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static devlab.app.auth.security.Constans.*;


@CrossOrigin
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {


    private UserServiceImpl userService;
    private PasswordEncoder bCryptPasswordEncoder;


    public WebSecurity(UserServiceImpl userService, PasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().disable();

        http
                .authorizeRequests()
                .anyRequest().permitAll()
                .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
                .anyRequest().authenticated()
                .anyRequest().permitAll()
              //  .antMatchers(BOOKS_SERVICE, FILES_UPLOAD).authenticated()
                .and()
                .formLogin().loginProcessingUrl(LOGIN)
               // .and().logout().deleteCookies("JSESSIONID")
                .and()
                //kazde zapytanie do sciezki zabezpieczonej przechodzi przez filtry
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .addFilter(new JWTAuthorizationFilter(authenticationManager()));
                //zarzadzanie sesjami
              //  .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS);

        // ALWAYS - sesja zostanie utworzona, jelsi jeszcze nie zostala utworzona.
        // ifRequired - sesja zostanie utworzona, w razie potrzeby (default)
        // NEVER - spring nigdy nie utorzy sesji, ale uzyje aktualnej sesji jeśli istnieje.
        // STATELESS - spring nie utworzy ani nie uzyj ezadnej sesji.

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userService)
                .passwordEncoder(bCryptPasswordEncoder);
    }


}
