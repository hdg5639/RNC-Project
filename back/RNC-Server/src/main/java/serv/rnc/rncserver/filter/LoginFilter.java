package serv.rnc.rncserver.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import serv.rnc.rncserver.dto.user.CustomUserDetails;
import serv.rnc.rncserver.dto.user.SignInDTO;
import serv.rnc.rncserver.dto.user.UserDTO;

import java.io.IOException;
import java.util.Collection;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    //JWTUtil 주입
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private UserDTO loginRequest;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, String customLoginUrl) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl(customLoginUrl); // 커스텀 URL 경로 설정
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        return loginRequest.getEmail();
    }

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        return loginRequest.getPassword();
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            loginRequest = objectMapper.readValue(request.getInputStream(), UserDTO.class);
        } catch (IOException e) {
            logger.error("Error parsing login request: ", e);
            throw new RuntimeException(e);
        }

        //클라이언트 요청에서 email, password 추출
        String email = obtainUsername(request);
        String password = obtainPassword(request);

        //스프링 시큐리티에서 email과 password를 검증하기 위해서는 token에 담아야 함
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);

        //token에 담은 검증을 위한 AuthenticationManager로 전달
        return authenticationManager.authenticate(authToken);
    }

    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        try {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            String email = customUserDetails.getUsername();

            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            String role = authorities.stream().findFirst().map(GrantedAuthority::getAuthority).orElse("ROLE_USER");

            // JWT 생성
            String token = jwtUtil.createJwt(email, role);

            // 응답에 JSON으로 token 포함
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String jsonResponse = SignInDTO.builder()
                    .username(customUserDetails.getUsername())
                    .email(email)
                    .token(token)
                    .build()
                    .toString();
//                    String.format(
//                    "{" +
//                            "\"isSuccess\": \"성공\", " +
//                            "\"token\": \"Bearer %s\"" +
//                            "}", token);

            response.getWriter().write(jsonResponse);
            response.getWriter().flush();
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Internal server error\"}");
            response.getWriter().flush();
        }
    }


    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        //로그인 실패시 401 응답 코드 반환
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = "{\"isSuccess\": \"실패\", \"message\": \"" + failed.getMessage() + "\"}";

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
        logger.info("로그인 실패");
    }
}
