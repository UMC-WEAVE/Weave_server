
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
        List<TeamResponse.getMemberList> memberList = teamService.getMembers(teamIdx);
        return ResponseEntity.ok(new JsonResponse(200, "Success", memberList));
    }

    // SHOW MY TEAMS
    @GetMapping("/teams/{userIdx}")
    public ResponseEntity<?> getTeam(@PathVariable("userIdx") Long userIdx){
        List<TeamResponse.teamResponse> teamList = teamService.getMyTeams(userIdx);
        return ResponseEntity.ok(new JsonResponse(200, "Success", teamList));
    }

    // DELETE TEAM
    @DeleteMapping("/teams/{teamIdx}")
    public ResponseEntity<?> deleteTeam(@PathVariable("teamIdx") Long teamIdx, @RequestBody TeamRequest.deleteTeamReq req){
        teamService.deleteTeam(teamIdx, req);
        return ResponseEntity.ok(new JsonResponse(200, "Success", null));
    }


    // DELETE TEAM MEMBER
    @DeleteMapping("/teams/{teamIdx}/members")
    public ResponseEntity<?> deleteMember(@PathVariable("teamIdx") Long teamIdx, @RequestBody TeamRequest.deleteMemberReq req){
        teamService.deleteMember(teamIdx, req);
        return ResponseEntity.ok(new JsonResponse(200, "Success", null));
    }

    // MODIFY TEAM INFO
    @PatchMapping("/teams/{teamIdx}/modify")
    public ResponseEntity<?> updateTeam(@PathVariable("teamIdx") Long teamIdx, @RequestBody TeamRequest.updateTeamReq req){
        teamService.updateTeam(teamIdx, req);
        return ResponseEntity.ok(new JsonResponse(200, "Success", null));
    }
}
