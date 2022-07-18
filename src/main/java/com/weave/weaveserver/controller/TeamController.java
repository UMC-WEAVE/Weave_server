package com.weave.weaveserver.controller;

import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.dto.TeamRequest;
import com.weave.weaveserver.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> addMember(@PathVariable int teamIdx, @RequestBody TeamRequest.addMemberReq req) {
        System.out.println(teamIdx);
        teamService.addMember(teamIdx, req);
        return ResponseEntity.ok(new JsonResponse(200, "addMember",null));
    }


}
