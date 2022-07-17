package com.weave.weaveserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuth2Controller {

    @GetMapping("/auth/kakao")
    public String kakaoRedirect(){
        return "redirect:http://localhost:8080/oauth2/authorization/kakao";
    }

    @GetMapping("/oauth/login/oauth2/code/kakao")
    public String kakao(){
        return "/oauth/login/oauth2/code/kakao";
    }
//
//    @GetMapping("/oauth/loginInfo")
//    @ResponseBody
//    public String oauthLoginInfo(Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth2UserPrincipal){
//        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//        Map<String, Object> attributes = oAuth2User.getAttributes();
//        System.out.println(attributes);
//        // PrincipalOauth2UserService의 getAttributes내용과 같음
//
//        Map<String, Object> attributes1 = oAuth2UserPrincipal.getAttributes();
//        // attributes == attributes1
//
//        return attributes.toString();     //세션에 담긴 user가져올 수 있음음
//    }
}
