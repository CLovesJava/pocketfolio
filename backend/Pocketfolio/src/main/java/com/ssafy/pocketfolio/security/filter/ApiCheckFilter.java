package com.ssafy.pocketfolio.security.filter;

import com.ssafy.pocketfolio.security.util.JWTUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;


@Log4j2
public class ApiCheckFilter extends OncePerRequestFilter {

    private AntPathMatcher antPathMatcher;
    private String[] patterns;
    private String[] postForGuestPatterns;
    private JWTUtil jwtUtil;

    @Value("${server.servlet.context-path:''}")
    private String contextPath;

    public ApiCheckFilter(String[] patterns, String[] postForGuestPatterns, JWTUtil jwtUtil){
        this.antPathMatcher = new AntPathMatcher();
        this.patterns = patterns;
        this.postForGuestPatterns = postForGuestPatterns;
        this.jwtUtil = jwtUtil;
    }

    public ApiCheckFilter(String[] postForGuestPatterns, JWTUtil jwtUtil){
        this.antPathMatcher = new AntPathMatcher();
        this.postForGuestPatterns = postForGuestPatterns;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        log.info("REQUEST URI: " + requestURI);
        if (antPathMatcher.match(contextPath + "/users/refresh", requestURI)) {
            log.info("refresh token");
            Long userSeq = checkAuthHeaderAndExtractUserSeq(request);
            if (userSeq > 0) {
                try {
                    String accessToken = jwtUtil.generateAccessToken(Long.toString(userSeq));
                    request.setAttribute("refreshToken", request.getHeader("Authorization").substring(7));
                    request.setAttribute("accessToken", accessToken);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    userSeq = -1L;
                }
            }
            request.setAttribute("userSeq", userSeq);
        } else {
            log.info("this uri need token: " + needToken(request));

            boolean isNeedToken = needToken(request);
            log.info("this uri need token: " + isNeedToken);

            if (isNeedToken) {

                log.info("ApiCheckFilter.................................................");

                Long userSeq = checkAuthHeaderAndExtractUserSeq(request);
                request.setAttribute("userSeq", userSeq); // setAttribute 위치 어디로 할지 고민 1

            }
        }

        filterChain.doFilter(request, response);
    }

    private long checkAuthHeaderAndExtractUserSeq(HttpServletRequest request) {

        long userSeq = -1L;

        String authHeader = request.getHeader("Authorization");

        if (!StringUtils.hasText(authHeader)) {
            return 0L; // 게스트
        }

        if (authHeader.startsWith("Bearer ")) {
            log.info("Authorization(accessToken) exist: " + authHeader);

            String token = authHeader.substring(7);
            if ("null".equals(token)) {
                return 0L; // 게스트
            }

            userSeq = jwtUtil.validateAndExtractUserSeq(token);
            log.info("validate result: " + userSeq);
        }

        return userSeq;
    }

    private boolean needToken(HttpServletRequest request) throws ServletException {
        String method = request.getMethod();
        if ("PATCH".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
            return true;
        }
        String requestURI = request.getRequestURI();
        if ("POST".equalsIgnoreCase(method)) {
            if (Arrays.stream(postForGuestPatterns).anyMatch(e -> antPathMatcher.match(e, requestURI))) {
                return false;
            }
            return true;
        }

//        return Arrays.stream(patterns).anyMatch(e -> antPathMatcher.match(e, requestURI));
        return true;
    }
}
