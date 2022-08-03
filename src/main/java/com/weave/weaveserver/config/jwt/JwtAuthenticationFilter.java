package com.weave.weaveserver.config.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 시큐리티에 UsernamePasswordAuthenticationFilter가 있음
// login요청해서 username, password 전송하면 filter가 동작함
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final  TokenService tokenService;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        //헤더에서 JWT를 받아옵니다.
        String token = tokenService.resolveToken(request);
        System.out.println("jwtFilter 탐!"+token);
        //유효한 토큰인지 확인합니다.
        if (token != null && tokenService.validateToken(token, request)) {
            //토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
            Authentication authentication = tokenService.getAuthentication(token);
            //securityContext에 Authentication객체를 저장합니다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        try{
            System.out.println("filterChain ㅌㅏㅁ!!!!!");
            filterChain.doFilter(request,response);
        }catch (ExpiredJwtException e){
            log.info("ExpiredJwt at JWT Filter");
        }
    }
}
