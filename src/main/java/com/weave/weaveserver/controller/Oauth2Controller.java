package com.weave.weaveserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weave.weaveserver.config.exception.MethodNotAllowedException;
import com.weave.weaveserver.config.jwt.Token;
import com.weave.weaveserver.config.jwt.TokenService;
import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.dto.UserRequest;
import com.weave.weaveserver.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        log.info("[API] oauthLogin with security");
        String redirect_uri = "http://wave-weave.shop/oauth2/authorization/"+oauthId;
//        String redirect_uri = "http://localhost:8080/oauth2/authorization/"+oauthId;
        response.sendRedirect(redirect_uri);

    }

    //소셜 로그인 성공시 redirect uri -> token처리를 위해 만듬
    @GetMapping("/token")
    public ResponseEntity<JsonResponse> home(@PathParam("token") String token){
        log.info("[API]redirectSocialLogin");
//        response.sendRedirect("http://localhost:8080/hello",);
        Token accessToken = new Token(token);
        return ResponseEntity.ok(new JsonResponse(200,"loginSuccess",accessToken));
    }

    //http://wave-weave.shop/login?error
    @GetMapping("/login")
    public ResponseEntity<?> loginError(@PathParam("error")String error){
        log.error("[REJECT]webLoginError");
        throw new MethodNotAllowedException("로그인 실패");
    }


    //TODO : android login
    //10/4 : 탈퇴로직 구현시 refToken, accessToken 필요할 수도!!
    @PostMapping("/android/login/{loginId}")
    public ResponseEntity<JsonResponse> androidLogin(@RequestBody UserRequest.login login, @PathVariable String loginId) {
        log.info("[API]androidLogin");
        Token token = userService.androidLogin(login, loginId);
        return ResponseEntity.ok(new JsonResponse(200,token.getLoginId()+" login",token.getToken()));
    }

}
