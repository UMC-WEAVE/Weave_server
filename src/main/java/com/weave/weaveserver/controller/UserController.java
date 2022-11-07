package com.weave.weaveserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weave.weaveserver.config.exception.*;
import com.weave.weaveserver.config.jwt.TokenService;
import com.weave.weaveserver.domain.User;
import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.dto.ReasonRequest;
import com.weave.weaveserver.dto.UserResponse;
import com.weave.weaveserver.service.QuitReasonService;
import com.weave.weaveserver.service.UserProvider;
import com.weave.weaveserver.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final TokenService tokenService;

    private final UserService userService;

    private final UserProvider userProvider;

    private final QuitReasonService reasonService;

    private final ObjectMapper objectMapper;


    @GetMapping("/hello")
    public String helloTest(){
        return "hello";
    }


    @GetMapping("/mypage")
    public ResponseEntity<JsonResponse> loadMyPage(HttpServletRequest request){
        log.info("[API] deleteUser");
        String email = tokenService.getUserEmail(request);
        UserResponse.myPage data = userService.loadMyPage(email);
        return ResponseEntity.ok(new JsonResponse(200, "loadMyPage",data));
    }

    @DeleteMapping("")
    public ResponseEntity<JsonResponse> deleteUser(HttpServletRequest req, @RequestBody ReasonRequest reason){
        log.info("[API] deleteUser");
        try{
            String email = tokenService.getUserEmail(req);
            User user = userService.getUserByEmail(email);
            userProvider.deleteUser(email);
        }catch (NullPointerException e){
            log.info("[REJECT]삭제된 유저");
            throw new BadRequestException("등록되지 않은 유저입니다.");
        }

        reasonService.addQuitReason(reason);
        return ResponseEntity.ok(new JsonResponse(200, "deleteUser",reason));
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
