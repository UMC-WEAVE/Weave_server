package com.weave.weaveserver.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.weave.weaveserver.config.exception.BadRequestException;
import com.weave.weaveserver.config.exception.NotFoundException;
import com.weave.weaveserver.config.jwt.TokenService;
import com.weave.weaveserver.domain.User;
import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.dto.TeamRequest;
import com.weave.weaveserver.dto.TeamResponse;
import com.weave.weaveserver.service.TeamService;
import com.weave.weaveserver.service.UserProvider;
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

    @Autowired
    private TeamService teamService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private TokenService tokenService;


    private User findUserByToken(HttpServletRequest httpServletRequest){
        String userUuid = tokenService.getUserUuid();
        log.info("[INFO] findUserByToken 호출 : " + userUuid);

        User user = userProvider.getUserByUuid(userUuid);

        if(user == null){
            log.info("[REJECT] findUserByToken : 해당 유저가 존재하지 않음");
            throw new NotFoundException("해당 uuid 사용자가 존재하지 않음");
        }
        return user;
    }

    // 팀원 초대, 삭제 시 사용되는 메서드
    private User findUserByEmail(String userEmail){
        User user = userProvider.getUserByEmail(userEmail);
        if(user == null){
            log.info("[REJECT] findUserByEmail : 해당 이메일을 가진 유저가 존재하지 않음");
            throw new NotFoundException("초대하려는 팀원이 존재하지 않습니다.");
        }
        log.info("[INFO] findUserByEmail 호출 : " + userEmail);
        return user;
    }

    // CREATE TEAM
    @PostMapping("/teams/create")
    public ResponseEntity<JsonResponse> createTeam(HttpServletRequest httpServletRequest,
                                                   @RequestPart TeamRequest.createReq req,
                                                   @RequestPart("fileName") @Nullable String fileName,
                                                   @RequestPart("file") @Nullable MultipartFile file) throws IOException, FirebaseAuthException {
        if(req.getTitle() == null){
            log.info("[REJECT] createTeam : 팀의 title이 존재하지 않음");
            throw new BadRequestException("title이 존재하지 않습니다.");
        }
        if(req.getStartDate() == null){
            log.info("[REJECT] createTeam : 팀의 startDate가 존재하지 않음");
            throw new BadRequestException("startDate가 존재하지 않습니다.");
        }
        if(req.getEndDate() == null){
            log.info("[REJECT] createTeam : 팀의 endDate가 존재하지 않음");
            throw new BadRequestException("endDate가 존재하지 않습니다.");
        }

        log.info("[API] createTeam : 팀 생성");
        Long teamIdx = teamService.createTeam(findUserByToken(httpServletRequest), req, fileName, file);
        return ResponseEntity.ok(new JsonResponse(200, "Success, createTeam",teamIdx));
    }

    // INVITE TEAM MEMBER
    @PostMapping("/teams/{teamIdx}/invite")
    public ResponseEntity<JsonResponse> addMember(@PathVariable("teamIdx") Long teamIdx,
                                                  @RequestBody TeamRequest.memberEmailReq req,
                                                  HttpServletRequest httpServletRequest) {

        log.info("[API] addMember : 팀원 초대");
        User inviter = findUserByToken(httpServletRequest);
        User invitedUser = findUserByEmail(req.getEmail());
        Long addUserIdx = teamService.addMember(teamIdx, req, inviter, invitedUser);
        return ResponseEntity.ok(new JsonResponse(200, "Success", addUserIdx));
    }

    // SHOW TEAM MEMBER LIST
    @GetMapping("/teams/{teamIdx}/members")
    public ResponseEntity<JsonResponse> getMembers(@PathVariable("teamIdx") Long teamIdx){
        log.info("[API] getMembers : 팀에 속한 팀원 전체 조회");
        List<TeamResponse.getMemberList> memberList = teamService.getMembers(teamIdx);
        return ResponseEntity.ok(new JsonResponse(200, "Success", memberList));
    }

    // SHOW MY TEAMS
    @GetMapping("/teams")
    public ResponseEntity<JsonResponse> getMyTeams(HttpServletRequest httpServletRequest){
        log.info("[API] getMyTeams : 내가 속한 팀 조회");
        TeamResponse.showMyTeamList teamList = teamService.getMyTeams(findUserByToken(httpServletRequest));
        return ResponseEntity.ok(new JsonResponse(200, "Success", teamList));
    }

    // DELETE TEAM
    @DeleteMapping("/teams/{teamIdx}")
    public ResponseEntity<JsonResponse> deleteTeam(@PathVariable("teamIdx") Long teamIdx, HttpServletRequest httpServletRequest){
        log.info("[API] deleteTeam : 팀 삭제");
        Long deleteTeamIdx = teamService.deleteTeam(teamIdx, findUserByToken(httpServletRequest));
        return ResponseEntity.ok(new JsonResponse(200, "Success", deleteTeamIdx));
    }


    // DELETE TEAM MEMBER
    @DeleteMapping("/teams/{teamIdx}/members")
    public ResponseEntity<JsonResponse> deleteMember(@PathVariable("teamIdx") Long teamIdx,
                                                     @RequestBody TeamRequest.memberEmailReq req,
                                                     HttpServletRequest httpServletRequest){
        log.info("[API] deleteMember : 팀원 삭제(팀에서 접근)");
        System.out.println(req.getEmail());
        User deletedUser = findUserByEmail(req.getEmail());
        String deleteMember = teamService.deleteMember(teamIdx, req, findUserByToken(httpServletRequest), deletedUser);
        return ResponseEntity.ok(new JsonResponse(200, "Success", deleteMember));
    }

    // MODIFY TEAM INFO
    @PatchMapping("/teams/{teamIdx}/modify")
    public ResponseEntity<JsonResponse> updateTeam(@PathVariable("teamIdx") Long teamIdx,
                                                   @RequestPart TeamRequest.updateTeamReq req,
                                                   @RequestPart ("fileName") @Nullable String fileName,
                                                   @RequestPart ("file") @Nullable MultipartFile file,
                                                   HttpServletRequest httpServletRequest) throws IOException, FirebaseAuthException{

        log.info("[API] updateTeam : 팀 정보 수정");
        Long updateTeamIdx = teamService.updateTeam(teamIdx, req, fileName, file, findUserByToken(httpServletRequest));
        return ResponseEntity.ok(new JsonResponse(200, "Success", updateTeamIdx));
    }
}