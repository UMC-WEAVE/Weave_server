package com.weave.weaveserver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weave.weaveserver.config.andOauth.KakaoProfile;
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
    @PostMapping("/android/login/kakao")
    public ResponseEntity<JsonResponse> KakaoLogin(@PathParam(value = "accessToken") String accessToken) {

        RestTemplate rt = new RestTemplate(); //http 요청을 간단하게 해줄 수 있는 클래스
        // 5, 6, 7 : 발급받은 Access 토큰으로 API를 호출해서 사용자의 정보를 응답으로 받는 코드
        HttpHeaders headersForRequestProfile = new HttpHeaders();
        headersForRequestProfile.add("Authorization", "Bearer " + accessToken);
        headersForRequestProfile.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoResourceProfileRequest = new HttpEntity<>(headersForRequestProfile);

        // Http 요청하기 - POST 방식으로 - 그리고 response 변수의 응답을 받음.
        ResponseEntity<String> resourceProfileResponse = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoResourceProfileRequest,
                String.class
        );

        KakaoProfile profile = null;
        try {
            profile = objectMapper.readValue(resourceProfileResponse.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String email = profile.getKakao_account().getEmail();

        User user = userService.getUserByEmail(email);
        if(user==null){
            log.info("kakao join : "+email);
            UserRequest.join joinUser = UserRequest.join.builder()
                    .email(email).loginId("kakao")
                    .name(profile.getKakao_account().getProfile().getNickname())
                    .image(profile.getProperties().getProfile_image())
                    .build();
            userService.joinUser(joinUser);
        }else{
            log.info("kakao login : "+email);
        }

        Token token = tokenService.generateToken(email);

        return ResponseEntity.ok(new JsonResponse(200,"kakao login",token.getToken()));
    }

}
