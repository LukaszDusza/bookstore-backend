package devlab.app.auth.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import devlab.app.auth.User.UserApp;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;


public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            UserApp userApp = new ObjectMapper().readValue(request.getInputStream(), UserApp.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userApp.getUsername(),
                            userApp.getPassword(),
                            new ArrayList<>() //GrantedAuthority - odp. za role userów. ROLE_ADMIN, ROLE_USER
                    )
            );

        } catch (IOException e) {
            throw new RuntimeException(e);
            //   e.printStackTrace();
        }
    }


    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication auth) throws IOException, ServletException {

        String token = JWT.create().withSubject(
                ((User) auth.getPrincipal()).getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + Constans.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(Constans.SECRET.getBytes()));

        response.addHeader("access-control-expose-headers", "Authorization");

        response.addHeader(Constans.AUTH_HEADER, Constans.TOKEN_PREFIX + token);
        response.addHeader("UserApp", ((User) auth.getPrincipal()).getUsername());

        System.out.println(Constans.TOKEN_PREFIX + token);

        System.out.println(JWT.decode(token).getPayload());

        //  response.addCookie(new Cookie("Ciasteczko", "Moje_pyszne_ciasteczko"));
        //  response.sendRedirect("http://www.wp.pl");

    }

}
