package com.lightcs.config;

import com.lightcs.filter.HeaderFilter;
import com.lightcs.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;

/**
 * @author peak-like
 * @create 2025/1/6
 * @Description 3.1.0 版本的 SecurityConfig 配置类
 **/
@EnableWebSecurity
@EnableMethodSecurity           // 使注解@PreAuthorize 生效
@Configuration
public class SecurityConfig {
    @Autowired
    private UserDetailsService userService;
    //    @Autowired
//    private HeaderFilter headerFilter;
    @Autowired
    private RedisUtil redisUtil;

    //基于组件的安全配置
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())  // 禁用 CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/login", "/user/register",
                                "/doc.html", "/webjars/**", "/swagger-ui/**",
                                "/static/**", "/v3/api-docs/**", "/**").permitAll()  // 公开访问
                        .anyRequest().authenticated()  // 其他接口需认证
                )
                .addFilterBefore(new HeaderFilter(redisUtil), UsernamePasswordAuthenticationFilter.class)// 添加 HeaderFilter到过滤器链中，且在 UsernamePasswordAuthenticationFilter 之前
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));  // 添加跨域配置

        return http.build();
    }

    /**
     * 跨域配置
     *
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
//        configuration.setAllowedOrigins(List.of("*")); // 允许所有来源
        configuration.setAllowedMethods(List.of("POST", "GET", "PUT", "OPTIONS", "DELETE", "CONNECT")); // 允许的方法
        configuration.setAllowedHeaders(List.of("*")); // 允许的头部设置
        configuration.setAllowCredentials(true); // 是否发送cookie
        configuration.setMaxAge(Duration.ofHours(1)); // 预检间隔时间

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {//不使用这种配置的原因是 访问/static/**的时候会被拦截
//        // 不拦截的请求路径
////        return (web) -> web.ignoring().requestMatchers("/user/login", "/user/register", "/doc.html", "/webjars/**", "/swagger-ui/**", "/static/**", "/v3/api-docs/**");
//        return (web) -> web.ignoring().requestMatchers("/**");
//    }

    @Bean
    AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userService);
        ProviderManager pm = new ProviderManager(daoAuthenticationProvider);
        return pm;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // 使用 BCrypt 进行密码加密, 里面可以添加参数表示加密强度
    }
}

