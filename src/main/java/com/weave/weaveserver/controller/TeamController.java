package com.weave.weaveserver.controller;

import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.dto.TeamRequest;
import com.weave.weaveserver.dto.TeamResponse;
import com.weave.weaveserver.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<?> createTeam(@RequestBody TeamRequest.createReq req){
        teamService.createTeam(req);
        return ResponseEntity.ok(new JsonResponse(200, "createTeam",null));
    }

    @PostMapping("/{teamIdx}")
    public ResponseEntity<?> addMember(@PathVariable Long teamIdx, @RequestBody TeamRequest.addMemberReq req) {
        System.out.println(teamIdx);
        teamService.addMember(teamIdx, req);
        return ResponseEntity.ok(new JsonResponse(200, "addMember",null));
    }

    @GetMapping("/{teamIdx}")
    public ResponseEntity<?> getMemberList(@PathVariable Long teamIdx) {
        System.out.println(teamIdx);
        List<TeamResponse.getMemberList> res = teamService.getMembers(teamIdx);
        return ResponseEntity.ok(new JsonResponse(200, "addMember", res));
    }



}
