package com.weave.weaveserver.config.jwt;

import com.weave.weaveserver.config.exception.BadRequestException;
import com.weave.weaveserver.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenService{
    private final UserService userService;
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

        return new Token(acToken, "refreshToken");
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
            throw new BadRequestException("잘못된 토큰값");
        }
        String findUser = userService.getUserByEmail(email).getEmail();
        if(!findUser.equals(email)){
            throw new BadRequestException("유저의 정보를 찾을 수 없음");
        }
        return email;
    }
}