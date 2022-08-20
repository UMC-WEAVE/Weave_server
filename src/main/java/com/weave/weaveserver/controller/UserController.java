package com.weave.weaveserver.controller;

import com.weave.weaveserver.config.exception.*;
import com.weave.weaveserver.config.jwt.Token;
import com.weave.weaveserver.config.jwt.TokenService;
import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.dto.UserResponse;
import com.weave.weaveserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    @GetMapping("/hello")
    public String helloTest(){
        return "hello";
    }


    @GetMapping("/mypage")
    public ResponseEntity<JsonResponse> loadMyPage(HttpServletRequest request){
        String email = tokenService.getUserEmail(request);
        UserResponse.myPage data = userService.loadMyPage(email);
        return ResponseEntity.ok(new JsonResponse(200, "loadMyPage",data));
    }

    @DeleteMapping("")
    public ResponseEntity<JsonResponse> deleteUser(HttpServletRequest request){
        String email = tokenService.getUserEmail(request);
        userService.deleteUser(email);
        return ResponseEntity.ok(new JsonResponse(200, "deleteUser",null));
    }


    ////throw error example
    @GetMapping("/error/{errorCode}")
    public void errorTest(@PathVariable int errorCode){
        switch (errorCode){
            case 400: throw new BadRequestException("400 : BadRequestException");
            case 403: throw new ForbiddenException("403 : ForbiddenException");
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
