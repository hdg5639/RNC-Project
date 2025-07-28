package serv.rnc.rncserver.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import serv.rnc.rncserver.filter.JwtFilter;
import serv.rnc.rncserver.filter.JwtUtil;
import serv.rnc.rncserver.filter.LoginFilter;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    //JWTUtil 주입
    private final JwtUtil jwtUtil;
    //AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests

                                // POST 요청만 허용
                                .requestMatchers(HttpMethod.POST,
                                        "/api/users/signup")
                                .permitAll()

                                // GET 요청만 허용
                                .requestMatchers(HttpMethod.GET,
                                        "/todo/todo/user")
                                .permitAll()

                                .requestMatchers(HttpMethod.PATCH,
                                        "/todo/category")
                                .permitAll()

                                .requestMatchers(HttpMethod.PUT,
                                        "/todo/todo/sympathy")
                                .permitAll()

                                .requestMatchers(HttpMethod.DELETE,
                                        "/todo/category",
                                        "/todo/todo")
                                .permitAll()
                                // 그 외의 요청은 인증 필요
                                .anyRequest().authenticated()
                )
                .formLogin(withDefaults())
                .logout(withDefaults())
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
                .cors(withDefaults());
        http
                .cors()
        http
                .addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class);
        http
                .addFilterAt(new LoginFilter(authenticationManager, jwtUtil, "/api/users/signin"), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://refresh-f5.store", "http://127.0.0.1:3000",
                "https://superlative-entremet-ac0250.netlify.app"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.addAllowedHeader("*");
        configuration.addAllowedOriginPattern("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//        return configuration.getAuthenticationManager();
//    }
}
