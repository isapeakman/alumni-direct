package com.lightcs.filter;

import com.lightcs.model.vo.UserVO;
import com.lightcs.utils.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

import static com.lightcs.constants.RedisConstant.TOKEN_PREFIX;
import static com.lightcs.constants.UserConstant.ADMIN;
import static com.lightcs.constants.UserConstant.USER;

/**
 * 请求头过滤器
 */
//@Component
@Slf4j
//@Component
public class HeaderFilter extends OncePerRequestFilter {
    //    @Autowired
    private RedisUtil redisUtil;

    public HeaderFilter(RedisUtil redisUtil) {//构造器注入
        this.redisUtil = redisUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("HeaderFilter start: {}", request.getRequestURI());
        // 从请求头中获取token
        String token = request.getHeader("Authorization");
        if (token == null) {
            // 放行路径 继续执行 不会报错; 未放行路径进入则会报错
            filterChain.doFilter(request, response);
            return;
        }
        // 从redis中获取用户信息
        Object o = redisUtil.get(TOKEN_PREFIX + token);
        if (o == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is invalid");
            return;
        }
        UserVO user = (UserVO) o;

        // 设置角色
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole() == 0 ? ADMIN : USER);
        ArrayList<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
        simpleGrantedAuthorities.add(authority);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user, null, simpleGrantedAuthorities);

        // 将用户信息存入上下文
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);// 给后面的UsernamePasswordAuthenticationFilter传值
        filterChain.doFilter(request, response);
        log.info("HeaderFilter end: {}", request.getRequestURI());
    }
}
