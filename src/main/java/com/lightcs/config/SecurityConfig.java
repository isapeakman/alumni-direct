package com.lightcs.config;

/**
 * @author peak-like
 * @create 2025/1/6
 * @Description 3.1.0 版本的 SecurityConfig 配置类
 **/

import com.lightcs.filter.HeaderFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    @Autowired
    private UserDetailsService userService;
    @Autowired
    private HeaderFilter headerFilter;


    //基于组件的安全配置
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())  // 禁用 CSRF
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/user/login").permitAll()  // 公开访问
                        .anyRequest().authenticated()  // 其他接口需认证
                )
                .addFilterBefore(headerFilter, UsernamePasswordAuthenticationFilter.class); // 添加 HeaderFilter到 UsernamePasswordAuthenticationFilter 之前
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // 不拦截的请求路径
        return (web) -> web.ignoring().requestMatchers("/user/login", "/user/register");
    }

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

