
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
    public ResponseEntity<JsonResponse> createTeam( @RequestBody TeamRequest.createReq req,
                                         HttpServletRequest httpServletRequest){
//        if(req.getTitle() == null){
//            return ResponseEntity.ok(new JsonResponse(200, "Success, createTeam",null));
//        }

        return teamService.createTeam(req, httpServletRequest);
    }

    // INVITE TEAM MEMBER
    @PostMapping("/teams/{teamIdx}/invite")
    public ResponseEntity<JsonResponse> addMember(@PathVariable("teamIdx") Long teamIdx,
                                       @RequestBody TeamRequest.addMemberReq req,
                                       HttpServletRequest httpServletRequest) {

        return teamService.addMember(teamIdx, req, httpServletRequest);
    }

    // SHOW TEAM MEMBER LIST
    @GetMapping("/teams/{teamIdx}/members")
    public ResponseEntity<JsonResponse> getMember(@PathVariable("teamIdx") Long teamIdx){
        List<TeamResponse.getMemberList> memberList = teamService.getMembers(teamIdx);
        return ResponseEntity.ok(new JsonResponse(200, "Success", memberList));
    }

    // SHOW MY TEAMS
    @GetMapping("/teams")
    public ResponseEntity<JsonResponse> getTeam(HttpServletRequest httpServletRequest){
        return teamService.getMyTeams(httpServletRequest);
    }

    // DELETE TEAM
    @DeleteMapping("/teams/{teamIdx}")
    public ResponseEntity<JsonResponse> deleteTeam(@PathVariable("teamIdx") Long teamIdx, HttpServletRequest httpServletRequest){
        return teamService.deleteTeam(teamIdx, httpServletRequest);
    }


    // DELETE TEAM MEMBER
    @DeleteMapping("/teams/{teamIdx}/members")
    public ResponseEntity<JsonResponse> deleteMember(@PathVariable("teamIdx") Long teamIdx,
                                          @RequestBody TeamRequest.deleteMemberReq req,
                                          HttpServletRequest httpServletRequest){
        return teamService.deleteMember(teamIdx, req, httpServletRequest);
    }

    // MODIFY TEAM INFO
    @PatchMapping("/teams/{teamIdx}/modify")
    public ResponseEntity<JsonResponse> updateTeam(@PathVariable("teamIdx") Long teamIdx,
                                        @RequestBody TeamRequest.updateTeamReq req,
                                        HttpServletRequest httpServletRequest){
        return teamService.updateTeam(teamIdx, req, httpServletRequest);
    }
}
