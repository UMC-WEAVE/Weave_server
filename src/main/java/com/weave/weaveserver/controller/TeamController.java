package com.weave.weaveserver.controller;

import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.dto.TeamRequest;
import com.weave.weaveserver.dto.TeamResponse;
import com.weave.weaveserver.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    // CREATE TEAM
    @PostMapping("/teams/{userIdx}/create")
    public ResponseEntity<?> createTeam(@PathVariable("userIdx") Long leaderIdx, @RequestBody TeamRequest.createReq req){
        teamService.createTeam(leaderIdx, req);
        return ResponseEntity.ok(new JsonResponse(200, "Success, createTeam",null));
    }

    // INVITE TEAM MEMBER
    @PostMapping("/teams/{teamIdx}/invite")
    public ResponseEntity<?> addMember(@PathVariable("teamIdx") Long teamIdx, @RequestBody TeamRequest.addMemberReq req) {
        int result = teamService.addMember(teamIdx, req);
        return ResponseEntity.ok(new JsonResponse(200, "Success", result));
    }

    // SHOW TEAM MEMBER LIST
    @GetMapping("/teams/{teamIdx}/members")
    public ResponseEntity<?> getMember(@PathVariable("teamIdx") Long teamIdx){
        List<?> memberList = teamService.getMemberList(teamIdx);
        return ResponseEntity.ok(new JsonResponse(200, "Success", memberList));
    }
}
