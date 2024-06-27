package com.sentinels.psychology.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinels.psychology.Jwt.JWTUtil;
import com.sentinels.psychology.member.domain.Refresh;
import com.sentinels.psychology.member.dto.CustomMemberDetails;
import com.sentinels.psychology.member.dto.reqMemberDto;
import com.sentinels.psychology.member.exception.MemberNotFoundException;
import com.sentinels.psychology.member.repository.RefreshRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public LoginFilter(AuthenticationManager authenticationManager, String defaultLoginUrl, JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        setFilterProcessesUrl(defaultLoginUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        try {
            reqMemberDto creds = new ObjectMapper().readValue(req.getInputStream(), reqMemberDto.class);
//            String username = obtainUsername(req);
//            String password = obtainPassword(req);
            String username = creds.getUsername();
            String password = creds.getPassword();

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //로그인 성공시 실행하는 메소드
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {


        CustomMemberDetails customUserDetails = (CustomMemberDetails) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();
//
        String access = jwtUtil.createJwt("access", username, role, 10 * 60 * 1000L);
        String refresh = jwtUtil.createJwt("refresh", username, role, 24 * 60 * 60 * 1000L);

        jwtUtil.addRefreshEntity(username, refresh, 24 * 60 * 60 * 1000L);
//        HTTP 인증 방식은 RFC 7235 정의에 따라 아래 인증 헤더 형태를 가져야 한다.
//        Authorization: 타입 인증토큰
//        Authorization: Bearer 인증토큰string
        response.addHeader("access", access);
        response.addCookie(jwtUtil.createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String errorMessage = "Authentication failed";

        if (failed instanceof BadCredentialsException) {
            errorMessage = "Invalid username or password";
        }

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", errorMessage);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }


}
