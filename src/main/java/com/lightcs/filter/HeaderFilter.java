package com.lightcs.filter;

import com.lightcs.model.vo.UserVO;
import com.lightcs.utils.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.lightcs.constants.RedisConstant.TOKEN_PREFIX;

/**
 * 请求头过滤器
 */
@Component
public class HeaderFilter extends OncePerRequestFilter {
    @Autowired
    private RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        Object o = redisUtil.get(TOKEN_PREFIX + token);
        if (o == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is invalid");
            return;
        }
        UserVO user = (UserVO) o;
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user, null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);// 给后面的UsernamePasswordAuthenticationFilter传值
        filterChain.doFilter(request, response);
    }
}
