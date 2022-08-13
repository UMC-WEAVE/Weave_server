
package com.weave.weaveserver.controller;

import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.dto.TeamRequest;
import com.weave.weaveserver.dto.TeamResponse;
import com.weave.weaveserver.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    // CREATE TEAM
    @PostMapping("/teams/create")
    public ResponseEntity<?> createTeam( @RequestBody TeamRequest.createReq req,
                                         HttpServletRequest httpServletRequest){
        teamService.createTeam(req, httpServletRequest);
        return ResponseEntity.ok(new JsonResponse(200, "Success, createTeam",null));
    }

    // INVITE TEAM MEMBER
    @PostMapping("/teams/{teamIdx}/invite")
    public ResponseEntity<?> addMember(@PathVariable("teamIdx") Long teamIdx,
                                       @RequestBody TeamRequest.addMemberReq req,
                                       HttpServletRequest httpServletRequest) {
        int result = teamService.addMember(teamIdx, req, httpServletRequest);

        return ResponseEntity.ok(new JsonResponse(200, "Success", result));
    }

    // SHOW TEAM MEMBER LIST
    @GetMapping("/teams/{teamIdx}/members")
    public ResponseEntity<?> getMember(@PathVariable("teamIdx") Long teamIdx){
        List<TeamResponse.getMemberList> memberList = teamService.getMembers(teamIdx);
        return ResponseEntity.ok(new JsonResponse(200, "Success", memberList));
    }

    // SHOW MY TEAMS
    @GetMapping("/teams")
    public ResponseEntity<?> getTeam(HttpServletRequest httpServletRequest){
        List<TeamResponse.teamResponse> teamList = teamService.getMyTeams(httpServletRequest);
        return ResponseEntity.ok(new JsonResponse(200, "Success", teamList));
    }

    // DELETE TEAM
    @DeleteMapping("/teams/{teamIdx}")
    public ResponseEntity<?> deleteTeam(@PathVariable("teamIdx") Long teamIdx, HttpServletRequest httpServletRequest){
        teamService.deleteTeam(teamIdx, httpServletRequest);
        return ResponseEntity.ok(new JsonResponse(200, "Success", null));
    }


    // DELETE TEAM MEMBER
    @DeleteMapping("/teams/{teamIdx}/members")
    public ResponseEntity<?> deleteMember(@PathVariable("teamIdx") Long teamIdx,
                                          @RequestBody TeamRequest.deleteMemberReq req,
                                          HttpServletRequest httpServletRequest){
        teamService.deleteMember(teamIdx, req, httpServletRequest);
        return ResponseEntity.ok(new JsonResponse(200, "Success", null));
    }

    // MODIFY TEAM INFO
    @PatchMapping("/teams/{teamIdx}/modify")
    public ResponseEntity<?> updateTeam(@PathVariable("teamIdx") Long teamIdx,
                                        @RequestBody TeamRequest.updateTeamReq req,
                                        HttpServletRequest httpServletRequest){
        teamService.updateTeam(teamIdx, req, httpServletRequest);
        return ResponseEntity.ok(new JsonResponse(200, "Success", null));
    }
}
