
package com.weave.weaveserver.controller;

import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.dto.TeamRequest;
import com.weave.weaveserver.dto.TeamResponse;
import com.weave.weaveserver.service.ImageService;
import com.weave.weaveserver.service.TeamService;
import com.weave.weaveserver.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TeamController {

    private final TeamService teamService;

    @Autowired
    private ImageService imageService;
    @Autowired
    private FileUtils fileUtils;

    // CREATE TEAM
    @PostMapping("/teams/create")
    public ResponseEntity<JsonResponse> createTeam(HttpServletRequest httpServletRequest,
                                                   @RequestPart TeamRequest.createReq req,
                                                   @RequestPart("fileName") @Nullable String fileName,
                                                   @RequestPart("file") @Nullable MultipartFile file) throws IOException {
        if(req.getTitle() == null){
            return ResponseEntity.ok(new JsonResponse(2004, "값을 모두 채워주세요(title)",null));
        }
        if(req.getStartDate() == null){
            return ResponseEntity.ok(new JsonResponse(2004, "값을 모두 채워주세요(startDate)",null));
        }
        if(req.getEndDate() == null){
            return ResponseEntity.ok(new JsonResponse(2004, "값을 모두 채워주세요(endDate)",null));
        }


        return teamService.createTeam(httpServletRequest, req, fileName, file);
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
                                        @RequestPart TeamRequest.updateTeamReq req,
                                        @RequestPart ("fileName") @Nullable String fileName,
                                        @RequestPart ("file") @Nullable MultipartFile file,
                                        HttpServletRequest httpServletRequest) throws IOException{

        return teamService.updateTeam(teamIdx, req, fileName, file, httpServletRequest);
    }
}
