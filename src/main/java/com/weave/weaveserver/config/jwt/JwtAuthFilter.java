//package com.weave.weaveserver.config.jwt;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.filter.GenericFilterBean;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import java.io.IOException;
//import java.util.Arrays;
//
//@RequiredArgsConstructor
//public class JwtAuthFilter extends GenericFilterBean {
//    private final TokenService tokenService;
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        System.out.println("JwtAuthFilter 접근");
//        String token = ((HttpServletRequest)request).getHeader("Auth");
//        System.out.println("JwtAuthFilter token : "+token);
//
//        if (token != null && tokenService.verifyToken(token)) {
//            String email = tokenService.getUid(token);
//
//            // DB연동을 안했으니 이메일 정보로 유저를 만들어주겠습니다
//            UserDto userDto = UserDto.builder()
//                    .email(email)
//                    .name("이름이에용")
//                    .picture("프로필 이미지에요").build();
//
//            Authentication auth = getAuthentication(userDto);
//            SecurityContextHolder.getContext().setAuthentication(auth);
//        }
//        System.out.println("no token");
//        chain.doFilter(request, response);
//    }
//
//    public Authentication getAuthentication(UserDto member) {
//        return new UsernamePasswordAuthenticationToken(member, "",
//                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
//    }
//}