package com.weave.weaveserver.controller;

import com.weave.weaveserver.config.exception.BadRequestException;
import com.weave.weaveserver.config.exception.GlobalException;
import com.weave.weaveserver.config.exception.MethodNotAllowedException;
import com.weave.weaveserver.config.exception.NotFoundException;
import com.weave.weaveserver.config.jwt.JwtProperties;
import com.weave.weaveserver.config.jwt.TokenService;
import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.dto.UserRequest;
import com.weave.weaveserver.dto.UserResponse;
import com.weave.weaveserver.service.UserService;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;

@RestController
@RequestMapping("/user")
public class OAuth2Controller {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    //response없음 -> 로그인 처리 후에 사용자의 정보를 oauth로 반환
    @GetMapping("/{oauthId}")
    public void oauthLogin(@PathVariable String oauthId, HttpServletResponse response)throws IOException {
        String redirect_uri = "http://localhost:8080/oauth2/authorization/"+oauthId;
        response.sendRedirect(redirect_uri);
    }

    //소셜 로그인 성공시 redirect uri -> token처리를 위해 만듬
    @GetMapping("/")
    public String home(@PathParam("token") String token){
//        response.sendRedirect("http://localhost:8080/hello",);
        return token;
    }

    @GetMapping
    public ResponseEntity<JsonResponse> loadMyPage(HttpServletRequest request){
        String email = tokenService.getUserEmail(request);
        UserResponse.myPage data = userService.loadMyPage(email);
        return ResponseEntity.ok(new JsonResponse(200, "loadMyPage",data));
    }



    ////throw error example

    @GetMapping("/error/{errorCode}")
    public void errorTest(@PathVariable int errorCode){
        switch (errorCode){
            case 400: throw new BadRequestException("400 : BadRequestException");
            case 403: throw new BadRequestException("400 : BadRequestException");
            case 404: throw new NotFoundException("404 : 유저를 찾을 수 없음");
            case 405: throw new MethodNotAllowedException("405 : MethodNotAllowedException");
            case 500: throw new GlobalException("505 : GlobalException");
        }
    }

    ////resolve Token example
    @GetMapping("/getToken")
    public String getUserEmail(HttpServletRequest request){
            String userEmail = tokenService.getUserEmail(request);
            return userEmail;
    }

}
