package com.weave.weaveserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/oauth")
public class OAuth2Controller {

    @GetMapping("/google")
    public void kakaoLogin(HttpServletResponse response)throws IOException {
        String redirect_uri="http://localhost:8080/oauth2/authorization/google";
        response.sendRedirect(redirect_uri);
    }

    @GetMapping("/kakao")
    public void googleLogin(HttpServletResponse response)throws IOException {
        String redirect_uri="http://localhost:8080/oauth2/authorization/kakao";
        response.sendRedirect(redirect_uri);
    }

    @GetMapping("/naver")
    public void naverLogin(HttpServletResponse response)throws IOException {
        String redirect_uri="http://localhost:8080/oauth2/authorization/naver";
        response.sendRedirect(redirect_uri);
    }

}
