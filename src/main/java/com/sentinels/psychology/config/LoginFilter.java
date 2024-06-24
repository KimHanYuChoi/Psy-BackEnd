package com.sentinels.psychology.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinels.psychology.Jwt.JWTUtil;
import com.sentinels.psychology.member.dto.CustomMemberDetails;
import com.sentinels.psychology.member.dto.reqMemberDto;
import com.sentinels.psychology.member.exception.MemberNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, String defaultLoginUrl, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
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

        String token = jwtUtil.createJwt(username, role, 60*60*1000L);

//        HTTP 인증 방식은 RFC 7235 정의에 따라 아래 인증 헤더 형태를 가져야 한다.
//        Authorization: 타입 인증토큰
//        Authorization: Bearer 인증토큰string

        response.addHeader("Authorization", "Bearer " + token);
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
