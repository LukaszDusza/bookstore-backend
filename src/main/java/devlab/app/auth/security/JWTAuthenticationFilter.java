package devlab.app.auth.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import devlab.app.model.UserApp;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import static devlab.app.auth.security.Constans.*;

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
    protected void successfulAuthentication (

            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication auth) throws IOException, ServletException {

        String token = JWT.create().withSubject(
                ((User) auth.getPrincipal()).getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SECRET.getBytes()));


//        response.setHeader("Access-Control-Allow-Origin", "*");
//        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
//        response.setHeader("Access-Control-Max-Age", "3600");
//        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, x-requested-with, Cache-Control");


        response.setHeader(AUTH_HEADER, TOKEN_PREFIX + token);
        response.setHeader("access-control-expose-headers", AUTH_HEADER);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader(
                "Access-Control-Allow-Headers",
                "*"); //Origin, Accept, Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With

        response.setHeader("Access-Control-Allow-Credentials", "true");

        System.out.println(TOKEN_PREFIX + token);

    }

}
