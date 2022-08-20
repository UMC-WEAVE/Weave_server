package com.weave.weaveserver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weave.weaveserver.config.andOauth.GoogleProfile;
import com.weave.weaveserver.config.andOauth.KakaoProfile;
import com.weave.weaveserver.config.andOauth.NaverProfile;
import com.weave.weaveserver.config.exception.BadRequestException;
import com.weave.weaveserver.config.exception.MethodNotAllowedException;
import com.weave.weaveserver.config.jwt.Token;
import com.weave.weaveserver.config.jwt.TokenService;
import com.weave.weaveserver.domain.User;
import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.dto.UserRequest;
import com.weave.weaveserver.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class Oauth2Controller {
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final TokenService tokenService;


    //TODO : web
    //response없음 -> 로그인 처리 후에 사용자의 정보를 oauth로 반환
    @GetMapping("/login/{oauthId}")
    public void oauthLogin(@PathVariable String oauthId, HttpServletResponse response)throws IOException {
        String redirect_uri = "http://wave-weave.shop/oauth2/authorization/"+oauthId;
//        String redirect_uri = "http://localhost:8080/oauth2/authorization/"+oauthId;
        response.sendRedirect(redirect_uri);

    }

    //소셜 로그인 성공시 redirect uri -> token처리를 위해 만듬
    @GetMapping("/token")
    public ResponseEntity<JsonResponse> home(@PathParam("token") String token){
//        response.sendRedirect("http://localhost:8080/hello",);
        Token accessToken = new Token(token);
        return ResponseEntity.ok(new JsonResponse(200,"loginSuccess",accessToken));
    }

    //http://wave-weave.shop/login?error
    @GetMapping("/login")
    public ResponseEntity<?> loginError(@PathParam("error")String error){
        throw new MethodNotAllowedException("로그인 실패");
    }


    //TODO : android
    @PostMapping("/android/login/{loginId}")
    public ResponseEntity<JsonResponse> androidLogin(@PathParam(value = "accessToken") String accessToken, @PathVariable String loginId) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
        ResponseEntity<String> response;
        headers.add("Authorization", "Bearer " + accessToken);

        String email="";
        String redirect_uri="";
        KakaoProfile kakaoProfile = null;
        GoogleProfile googleProfile = null;
        NaverProfile naverProfile=null;

        UserRequest.join joinUser;

        if(loginId.equals("kakao")) {
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
            redirect_uri="https://kapi.kakao.com/v2/user/me";

            response=restTemplate.exchange(redirect_uri, HttpMethod.POST,request,String.class);

            try {
                kakaoProfile = objectMapper.readValue(response.getBody(), KakaoProfile.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            email=kakaoProfile.getKakao_account().getEmail();
            joinUser = UserRequest.join.builder()
                    .email(email)
                    .loginId("kakao")
                    .name(kakaoProfile.getProperties().getNickname())
                    .image(kakaoProfile.getProperties().getThumbnail_image()).build();
            System.out.println(response.getBody());

            User user = userService.getUserByEmail(email);
            if(user==null){
                userService.joinUser(joinUser);
            }else{
                log.info("kakao login : "+email);
            }
            Token token = tokenService.generateToken(email);
            return ResponseEntity.ok(new JsonResponse(200,"kakao login",token.getToken()));
        }
        if(loginId.equals("naver")) {
            redirect_uri="https://openapi.naver.com/v1/nid/me";
            response=restTemplate.exchange(redirect_uri, HttpMethod.POST,request,String.class);
            try {
                naverProfile = objectMapper.readValue(response.getBody(), NaverProfile.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            email=naverProfile.getResponse().getEmail();
            joinUser = UserRequest.join.builder()
                    .email(email)
                    .loginId("naver")
                    .name(naverProfile.getResponse().getName())
                    .image(naverProfile.getResponse().getProfile_image()).build();
            System.out.println("response.getBody() = " + response.getBody());

            User user = userService.getUserByEmail(email);
            if(user==null){
                userService.joinUser(joinUser);
            }else{
                log.info("naver login : "+email);
            }
            Token token = tokenService.generateToken(email);
            return ResponseEntity.ok(new JsonResponse(200,"naver login",token.getToken()));
        }

        if(loginId.equals("google")){
            redirect_uri="https://www.googleapis.com/oauth2/v1/userinfo";
            response=restTemplate.exchange(redirect_uri, HttpMethod.GET,request,String.class);
            try {
                googleProfile = objectMapper.readValue(response.getBody(), GoogleProfile.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            email= googleProfile.getEmail();
            joinUser = UserRequest.join.builder()
                    .email(email)
                    .loginId("google")
                    .name(googleProfile.getName())
                    .image(googleProfile.getPicture()).build();

            User user = userService.getUserByEmail(email);
            if(user==null){
                userService.joinUser(joinUser);
            }else{
                log.info("google login : "+email);
            }
            System.out.println(googleProfile);
            Token token = tokenService.generateToken(email);
            return ResponseEntity.ok(new JsonResponse(200,"google login",token.getToken()));
        }
        throw new MethodNotAllowedException("로그인 플랫폼이 잘못됨");
    }




//
//    @PostMapping("/android/login/{loginId}")
//    public Object androidLogin(@PathParam(value = "accessToken") String accessToken, @PathVariable String loginId) {
//
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
//        ResponseEntity<String> response;
//        headers.add("Authorization", "Bearer " + accessToken);
//
//        String email="";
//        String redirect_uri="";
//        KakaoProfile kakaoProfile = null;
//        GoogleProfile googleProfile = null;
//        NaverProfile naverProfile=null;
//
//        UserRequest.join joinUser;
//
//        if(loginId.equals("kakao")) {
//            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//            redirect_uri="https://kapi.kakao.com/v2/user/me";
//
//            response=restTemplate.exchange(redirect_uri, HttpMethod.POST,request,String.class);
//
//            try {
//                kakaoProfile = objectMapper.readValue(response.getBody(), KakaoProfile.class);
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
//            email=kakaoProfile.getKakao_account().getEmail();
//            joinUser = UserRequest.join.builder()
//                    .email(email)
//                    .loginId("kakao")
//                    .name(kakaoProfile.getProperties().getNickname())
//                    .image(kakaoProfile.getProperties().getThumbnail_image()).build();
//            System.out.println(response.getBody());
//            System.out.println("kakao profile : "+kakaoProfile.getKakao_account().getProfile().is_default_image());
//            User user = userService.getUserByEmail(email);
//            if(user==null){
//                userService.joinUser(joinUser);
//            }else{
//                log.info("kakao login : "+email);
//            }
//            Token token = tokenService.generateToken(email);
//            return ResponseEntity.ok(new JsonResponse(200,"kakao login",token.getToken()));
//        }
//        if(loginId.equals("naver")) {
//            redirect_uri="https://openapi.naver.com/v1/nid/me";
//            response=restTemplate.exchange(redirect_uri, HttpMethod.POST,request,String.class);
//            try {
//                naverProfile = objectMapper.readValue(response.getBody(), NaverProfile.class);
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
//            email=naverProfile.getResponse().getEmail();
//            joinUser = UserRequest.join.builder()
//                    .email(email)
//                    .loginId("naver")
//                    .name(naverProfile.getResponse().getName())
//                    .image(naverProfile.getResponse().getProfile_image()).build();
//            System.out.println("response.getBody() = " + response.getBody());
//
//            User user = userService.getUserByEmail(email);
//            if(user==null){
//                userService.joinUser(joinUser);
//            }else{
//                log.info("naver login : "+email);
//            }
//            Token token = tokenService.generateToken(email);
//            return ResponseEntity.ok(new JsonResponse(200,"naver login",token.getToken()));
//        }
//
//        if(loginId.equals("google")){
//            redirect_uri="https://www.googleapis.com/oauth2/v1/userinfo";
//            response=restTemplate.exchange(redirect_uri, HttpMethod.GET,request,String.class);
//            try {
//                googleProfile = objectMapper.readValue(response.getBody(), GoogleProfile.class);
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
//            email= googleProfile.getEmail();
//            joinUser = UserRequest.join.builder()
//                    .email(email)
//                    .loginId("google")
//                    .name(googleProfile.getName())
//                    .image(googleProfile.getPicture()).build();
//
//            User user = userService.getUserByEmail(email);
//            if(user==null){
//                userService.joinUser(joinUser);
//            }else{
//                log.info("google login : "+email);
//            }
//            System.out.println(googleProfile);
//            Token token = tokenService.generateToken(email);
//            return ResponseEntity.ok(new JsonResponse(200,"google login",token.getToken()));
//        }
//        throw new MethodNotAllowedException("로그인 플랫폼이 잘못됨");
//    }

}
