
package com.weave.weaveserver.controller;

import com.weave.weaveserver.config.exception.BadRequestException;
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
            log.error("[ERROR] createTeam - 팀의 title이 존재하지 않음");
            throw new BadRequestException("title이 존재하지 않습니다.");
        }
        if(req.getStartDate() == null){
            log.error("[ERROR] createTeam - 팀의 startDate가 존재하지 않음");
            throw new BadRequestException("startDate가 존재하지 않습니다.");
        }
        if(req.getEndDate() == null){
            log.error("[ERROR] createTeam - 팀의 endDate가 존재하지 않음");
            throw new BadRequestException("endDate가 존재하지 않습니다.");
        }

        Long teamIdx = teamService.createTeam(httpServletRequest, req, fileName, file);
        return ResponseEntity.ok(new JsonResponse(200, "Success, createTeam",teamIdx));
    }

    // INVITE TEAM MEMBER
    @PostMapping("/teams/{teamIdx}/invite")
    public ResponseEntity<JsonResponse> addMember(@PathVariable("teamIdx") Long teamIdx,
                                       @RequestBody TeamRequest.addMemberReq req,
                                       HttpServletRequest httpServletRequest) {

        Long addUserIdx = teamService.addMember(teamIdx, req, httpServletRequest);
        return ResponseEntity.ok(new JsonResponse(200, "Success", addUserIdx));
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
        List<TeamResponse.getMyTeams> teamList = teamService.getMyTeams(httpServletRequest);
        return ResponseEntity.ok(new JsonResponse(200, "Success", teamList));
    }

    // DELETE TEAMㅋ
    @DeleteMapping("/teams/{teamIdx}")
    public ResponseEntity<JsonResponse> deleteTeam(@PathVariable("teamIdx") Long teamIdx, HttpServletRequest httpServletRequest){
        Long deleteTeamIdx = teamService.deleteTeam(teamIdx, httpServletRequest);
        return ResponseEntity.ok(new JsonResponse(200, "Success", deleteTeamIdx));
    }


    // DELETE TEAM MEMBER
    @DeleteMapping("/teams/{teamIdx}/members")
    public ResponseEntity<JsonResponse> deleteMember(@PathVariable("teamIdx") Long teamIdx,
                                          @RequestBody TeamRequest.deleteMemberReq req,
                                          HttpServletRequest httpServletRequest){
        String deleteMember = teamService.deleteMember(teamIdx, req, httpServletRequest);
        return ResponseEntity.ok(new JsonResponse(200, "Success", deleteMember));
    }

    // MODIFY TEAM INFO
    @PatchMapping("/teams/{teamIdx}/modify")
    public ResponseEntity<JsonResponse> updateTeam(@PathVariable("teamIdx") Long teamIdx,
                                        @RequestPart TeamRequest.updateTeamReq req,
                                        @RequestPart ("fileName") @Nullable String fileName,
                                        @RequestPart ("file") @Nullable MultipartFile file,
                                        HttpServletRequest httpServletRequest) throws IOException{

        Long updateTeamIdx = teamService.updateTeam(teamIdx, req, fileName, file, httpServletRequest);
        return ResponseEntity.ok(new JsonResponse(200, "Success", updateTeamIdx));
    }
}
