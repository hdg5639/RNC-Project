package serv.rnc.rncserver.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import serv.rnc.rncserver.dto.user.CustomUserDetails;
import serv.rnc.rncserver.entity.User;
import serv.rnc.rncserver.enums.UserRole;

import java.io.IOException;
import java.util.List;

public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (isPermitted(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        //request에서 Authorization 헤더를 찾음
        String token = null;

        // Authorization 헤더에서 Bearer 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7); // "Bearer " 이후 토큰 값
        }

        //Authorization 헤더 검증
        if (token == null ) {

            filterChain.doFilter(request, response);

            //조건이 해당되면 메소드 종료 (필수)
            return;
        }


        //토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("token")) {
                    cookie.setValue(null);
                    cookie.setMaxAge(0); // 브라우저에 삭제 요청
                    response.addCookie(cookie);
                }
            }
            // 응답 코드 설정 + 메시지 전송
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\": \"토큰 소멸\"}");

            return; // 더 이상 필터 체인 타지 않도록 종료
        }
        //토큰에서 email과 role 획득
        String email = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        //userEntity를 생성하여 값 set
        User userEntity = User.builder()
                .username("tempuser")
                .email(email)
                .role(UserRole.valueOf(role))
                .password("temppassword")
                .build();

        //UserDetails에 회원 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    private static final List<String> WHITELIST = List.of(
            "/api/users/login",
            "/api/users/logout",
            "/api/users/isExist",
            "/api/users/signup",
            "/api/email",
            "/swagger-ui",
            "/docs",
            "/api-docs"
    );

    private boolean isPermitted(String uri) {
        return WHITELIST.stream().anyMatch(uri::startsWith);
    }
}
