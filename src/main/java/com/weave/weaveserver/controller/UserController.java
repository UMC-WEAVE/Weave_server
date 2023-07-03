package com.weave.weaveserver.controller;

import com.weave.weaveserver.config.exception.BadRequestException;
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

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final TokenService tokenService;

    private final UserService userService;

    private final UserProvider userProvider;

    private final QuitReasonService reasonService;



    @GetMapping("/hello")
    public String helloTest(){
        return "hello";
    }


    @GetMapping("/mypage")
    public ResponseEntity<JsonResponse> loadMyPage(){
        log.info("[API] getUser");
        String uuid = tokenService.getUserUuid();
        UserResponse.myPage data = userService.loadMyPage(uuid);
        return ResponseEntity.ok(new JsonResponse(200, "loadMyPage",data));
    }

    @DeleteMapping("")
    public ResponseEntity<JsonResponse> deleteUser(@RequestBody ReasonRequest reason){
        log.info("[API] deleteUser");
        try{
            String uuid = tokenService.getUserUuid();
            User user = userProvider.getUserByUuid(uuid);
            userService.deleteUser(uuid, user.getLoginId());
        }catch (NullPointerException e){
            log.info("[REJECT]삭제된 유저");
            throw new BadRequestException("등록되지 않은 유저입니다.");
        }
        reasonService.addQuitReason(reason);
        return ResponseEntity.ok(new JsonResponse(200, "deleteUser",reason));
    }



}
