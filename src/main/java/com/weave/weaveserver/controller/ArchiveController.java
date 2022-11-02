package com.weave.weaveserver.controller;

import com.weave.weaveserver.config.exception.ConflictException;
import com.weave.weaveserver.config.exception.ForbiddenException;
import com.weave.weaveserver.config.exception.NotFoundException;
import com.weave.weaveserver.config.exception.UnAuthorizedException;
import com.weave.weaveserver.config.jwt.TokenService;
import com.weave.weaveserver.domain.Archive;
import com.weave.weaveserver.domain.Team;
import com.weave.weaveserver.domain.User;
import com.weave.weaveserver.dto.ArchiveRequest;
import com.weave.weaveserver.dto.ArchiveResponse;
import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.service.ArchiveService;
import com.weave.weaveserver.service.TeamService;
import com.weave.weaveserver.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ArchiveController {
    private final ArchiveService archiveService;
    private final TokenService tokenService;
    private final UserService userService;
    private final TeamService teamService;
    
    //테스트용으로 작성했던 코드들
//    @GetMapping("/log/archives")
//    public ResponseEntity<Object> testLogger() {
//        log.info("[API] testLogger : called");
//        log.error("test error");
//        log.warn("test warn");
//        log.info("test info");
//        log.debug("test debug"); //이 레벨 부터는 출력 안되게 설정되어있음
//        log.trace("test trace");
//        return ResponseEntity.ok(new JsonResponse(200, "Test log success", null));
//    }

//    @PatchMapping("/archives/test/set-user-null")
//    public ResponseEntity<Object> testSetUserNull(HttpServletRequest servletRequest){
//        User clientUser = findUserByEmailInToken(servletRequest);
//        archiveService.setUserNullByUser(clientUser);
//        return ResponseEntity.ok(new JsonResponse(200, "testSetUserNull success", null));
//    }

    @PostMapping("/archives")
    public ResponseEntity<Object> createArchive(@RequestPart ArchiveRequest.createRequest request,
                                                @RequestPart("fileName") @Nullable String fileName,
                                                @RequestPart("file") @Nullable MultipartFile file,
                                                HttpServletRequest servletRequest) throws IOException { //RequestBody 에 입력값들을 담고, Header 에 유저의 토큰을 담아 보냄.
        log.info("[API] createArchive : android MultipartFile test log");
        //TODO : 어디까지 로그파일에 남겨도 되는 거지?? 아래 안드 테스트 코드는 굉장히 지저분한(자세한) 편인데 이런 것도 남기면 민폐인가?
        log.info("jh request : "+request);
        log.info("jh fileName : "+fileName);
        if(file != null){
            log.info("jh file : "+file);
            log.info("jh file.getContentType : "+file.getContentType());
            log.info("jh file.getSize : "+file.getSize());
        } else{
            log.info("jh file : file == null");
        }

        log.info("[API] createArchive : call addArchive");

        User clientUser = findUserByEmailInToken(servletRequest);

        Team team = teamService.findTeamByTeamIdx(request.getTeamIdx());
        if(team == null){
            log.info("[REJECT] addArchive : team == null");
            throw new ConflictException("Team is not found by the teamIdx in request body");
        }

        checkBelong(team.getTeamIdx(), clientUser.getEmail());

        archiveService.addArchive(request, fileName, file, team, clientUser);
        return ResponseEntity.ok(new JsonResponse(201, "Archive successfully created", null));
    }

    @GetMapping("/teams/{teamIdx}/archives")
    public ResponseEntity<Object> getArchiveList(@PathVariable Long teamIdx, HttpServletRequest servletRequest){
        log.info("[API] getArchiveList : call getArchiveList");

        User clientUser = findUserByEmailInToken(servletRequest);

        //Team
        Team team = teamService.findTeamByTeamIdx(teamIdx);
        if(team == null){
            log.info("[REJECT] getArchiveList : team == null");
            throw new NotFoundException("Team is not found by this teamIdx");
        }

        checkBelong(team.getTeamIdx(), clientUser.getEmail());

        ArchiveResponse.archiveListResponseContainer archiveListContainer = archiveService.getArchiveList(team);
        return ResponseEntity.ok(new JsonResponse(200, "success getArchiveList", archiveListContainer));
    }

    @Transactional //TODO : 여기에 이걸 붙여야 Service단에서 archive.getUser()할 때 LAZY관련 에러가 안남.. JPA공부 필요
    @GetMapping("/archives/{archiveIdx}")
    public ResponseEntity<Object> getArchiveDetail(@PathVariable Long archiveIdx, HttpServletRequest servletRequest){
        log.info("[API] getArchiveDetail : call getArchiveDetail");

        User clientUser = findUserByEmailInToken(servletRequest);

        Archive archive = archiveService.findByArchiveIdx(archiveIdx);
        if(archive == null){
            log.info("[REJECT] getArchiveDetail : archive == null");
            throw new NotFoundException("Archive is not found by this archiveIdx");
        }
        Team team = archive.getTeam();
        checkBelong(team.getTeamIdx(), clientUser.getEmail());

        ArchiveResponse.archiveResponse archiveDetail = archiveService.getArchiveDetail(archive);
        return ResponseEntity.ok(new JsonResponse(200, "success getArchiveDetail", archiveDetail));
    }

    @PatchMapping("/archives/{archiveIdx}/pin")
    public ResponseEntity<Object> updateArchivePin(@PathVariable Long archiveIdx, HttpServletRequest servletRequest){
        log.info("[API] updateArchivePin : call updateArchivePin");

        User clientUser = findUserByEmailInToken(servletRequest);

        Archive archive = archiveService.findByArchiveIdx(archiveIdx);
        if(archive == null){
            log.info("[REJECT] updateArchivePin : archive == null");
            throw new NotFoundException("Archive is not found by this archiveIdx");
        }
        Team team = archive.getTeam();
        checkBelong(team.getTeamIdx(), clientUser.getEmail());

        archiveService.updateArchivePin(archive);
        return ResponseEntity.ok(new JsonResponse(200, "success updateArchivePin", null));
    }

    @DeleteMapping("/archives/{archiveIdx}")
    public ResponseEntity<Object> deleteArchive(@PathVariable Long archiveIdx, HttpServletRequest servletRequest){
        log.info("[API] deleteArchive : call deleteArchive");
        User clientUser = findUserByEmailInToken(servletRequest);

        Archive archive = archiveService.findByArchiveIdx(archiveIdx);
        if(archive != null) { //archiveIdx에 해당하는 archive가 존재할 때만 belong체크와 삭제 실행
            Team team = archive.getTeam();
            checkBelong(team.getTeamIdx(), clientUser.getEmail());

        }
        else {
            log.info("[REJECT] deleteArchive : archive == null. Delete nothing");
            throw new NotFoundException("Archive is not found by this archiveIdx");
        }

        archiveService.deleteArchive(archive);
        return ResponseEntity.ok(new JsonResponse(204, "Archive successfully deleted", null));
    }


/////////-- API 외 메서드 --////////////

    private User findUserByEmailInToken(HttpServletRequest servletRequest){
        if(servletRequest == null){
            log.info("[REJECT] archive findUserByEmailInToken : servletRequest == null");
            throw new UnAuthorizedException("Unauthorized. HttpServletRequest is null");
        }
        String userEmail = tokenService.getUserEmail(servletRequest); // 토큰으로부터 user 이메일 가져오기
        User clientUser = userService.getUserByEmail(userEmail);

        return clientUser;
    }
    

    private void checkBelong(Long teamIdx, String email){
        boolean isBelong = teamService.findByTeamIdxAndUser(teamIdx, email);

        if(!isBelong){
            log.info("[REJECT] archive checkBelong : isBelong == false ");
            throw new ForbiddenException("Forbidden. User is not belong in the team");
        }
    }

}
