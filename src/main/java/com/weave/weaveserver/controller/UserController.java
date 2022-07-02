package com.weave.weaveserver.controller;

import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.dto.UserRequest;
import com.weave.weaveserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinUser(@RequestBody UserRequest.join joinUser){
        userService.joinUser(joinUser);
        return ResponseEntity.ok(new JsonResponse(200, "joinUser",null));
    }



}
