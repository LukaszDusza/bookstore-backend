package devlab.app.auth.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        String header = request.getHeader(Constans.AUTH_HEADER);

//        response.addHeader("Access-Control-Allow-Origin", "*");
//        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
//        response.addHeader("Access-Control-Allow-Credentials", "true");
//        response.addHeader("Access-Control-Allow-Headers",
//                "content-type, x-gwt-module-base, x-gwt-permutation, clientid, longpush");


        if (header == null || !header.startsWith(Constans.TOKEN_PREFIX)) {
                chain.doFilter(request, response);

            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(request);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        chain.doFilter(request, response);

    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

        String token = request.getHeader(Constans.AUTH_HEADER);

        if (token != null) {
            String userToken = JWT.require(Algorithm.HMAC512(Constans.SECRET.getBytes()))
                    .build()
                    .verify(token.replace(Constans.TOKEN_PREFIX, ""))
                    .getSubject();

            System.out.println(userToken); //wyswietli username


            if (userToken != null) {
                return new UsernamePasswordAuthenticationToken(userToken, null, new ArrayList<>());
            }

            return null;

        }

        return null;
    }

}
