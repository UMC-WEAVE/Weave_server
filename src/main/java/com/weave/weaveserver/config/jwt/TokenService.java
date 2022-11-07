package com.weave.weaveserver.config.jwt;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.weave.weaveserver.config.exception.BadRequestException;
import com.weave.weaveserver.config.exception.UnAuthorizedException;
import com.weave.weaveserver.config.exception.jwt.ExceptionCode;
import com.weave.weaveserver.repository.UserRepository;
import com.weave.weaveserver.service.UserService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenService{
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;

    private String secretKey = JwtProperties.SECRET;


    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }


    public Token generateToken(String email) {

        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", "ROLE_USER");
        Date now = new Date();

        String acToken = Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + JwtProperties.TEST_EXPIRATION_TIME)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();

        return new Token(acToken);
    }

    public String getUserEmail(HttpServletRequest request){
        String token = request.getHeader(JwtProperties.ACCESS_HEADER_STRING);
        String email;
        try{
            email = Jwts.parser().setSigningKey(JwtProperties.SECRET.getBytes()).parseClaimsJws(token).getBody().getSubject();
        }catch (NullPointerException e){
            System.out.println("NullPointerException");
            email = Jwts.parser().setSigningKey(JwtProperties.SECRET.getBytes()).parseClaimsJws(token).getBody().getSubject();
        }catch (SignatureException e){
            throw new UnAuthorizedException("UNAUTHORIZED");
        }
//        String findUser = userService.getUserByEmail(email).getEmail();
        try{
            String findUser = userRepository.findUserByEmail(email).getEmail();
            if(!findUser.equals(email)){
                throw new BadRequestException("유저의 정보를 찾을 수 없음");
            }
        }catch (NullPointerException e){
            throw new BadRequestException("등록되지 않은 유저입니다.");
        }

        return email;
    }

    //jwt토큰에서 인증정보 조회
    public Authentication getAuthentication(String token){
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
    }

    //토큰에서 회원 정보 추출
    public String getUserPk(String token){
        try{
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        }catch (NullPointerException e){
            log.info(e.getMessage());
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        }
    }

    //Request의 Header에서 token값을 가져옵니다. "X-AUTH-TOKEN":"TOKEN값" =>refresh token은 DB에 저장
    public String resolveToken(HttpServletRequest request){
        return request.getHeader(JwtProperties.ACCESS_HEADER_STRING);
    }

    //토큰의 유효성 + 만료 일자 확인
    public boolean validateToken(String jwtToken,HttpServletRequest request){
        try{
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        }catch (SecurityException e) {
            log.info("Invalid JWT signature.");
            request.setAttribute("exception", ExceptionCode.WRONG_TYPE_TOKEN.getStatus());
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
            request.setAttribute("exception", ExceptionCode.WRONG_TYPE_TOKEN.getStatus());
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token. at validation Token");
            request.setAttribute("exception", ExceptionCode.EXPIRED_TOKEN.getStatus());
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            request.setAttribute("exception", ExceptionCode.WRONG_TYPE_TOKEN.getStatus());
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            request.setAttribute("exception", ExceptionCode.UNSUPPORTED_TOKEN.getStatus());
        } catch (Exception e) {
            log.error("any exception");
            request.setAttribute("exception", ExceptionCode.UNSUPPORTED_TOKEN.getStatus());
        }
        return false;
    }

}