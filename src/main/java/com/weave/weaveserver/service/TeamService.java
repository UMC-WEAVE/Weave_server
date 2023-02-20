package com.weave.weaveserver.service;

import com.google.firebase.auth.FirebaseAuthException;
import com.weave.weaveserver.config.exception.BadRequestException;
import com.weave.weaveserver.config.exception.ForbiddenException;
import com.weave.weaveserver.config.exception.NotFoundException;
import com.weave.weaveserver.domain.*;
import com.weave.weaveserver.dto.PlanResponse;
import com.weave.weaveserver.dto.TeamRequest;
import com.weave.weaveserver.dto.TeamResponse;
import com.weave.weaveserver.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {

    @Autowired
    private FireBaseService imageService;
//    @Autowired
//    private UserService userService;
    @Autowired
    private ArchiveService archiveService;
    @Autowired
    private PlanService planService;

    public final TeamRepository teamRepository;

    public final BelongRepository belongRepository;

    private final PlanRepository planRepository;


    // [외부사용] findByTeamIdx -> plan, archive
    public Team findTeamByTeamIdx(Long teamIdx){
        return teamRepository.findByTeamIdx(teamIdx);
    }

    // [외부사용] findByTeamIdxAndUser -> archive
    public boolean findByTeamIdxAndUser(Long teamIdx, String userEmail){
        Belong belong = belongRepository.findByTeamIdxAndUser(teamIdx, userEmail);
        if(belong == null){
            return false;
        } else {
            return true;
        }
    }

    public boolean checkSameUser(User check1, User check2){
        log.info("[INFO] checkSameUser 호출");
        if(check1.getUserIdx().equals(check2.getUserIdx())){
            log.info("[INFO] checkSameUser : SAME -> " + check1.getUserIdx() + " == " + check2.getUserIdx());
            return true;
        }
        log.info("[INFO] checkSameUser : NOT SAME -> " + check1.getUserIdx() + " != " + check2.getUserIdx());
        return false;
    }


    public String uploadTeamImage(Team team, String fileName, MultipartFile file) throws IOException, FirebaseAuthException {

        // 기존 이미지 삭제
        if(team.getImgUrl()!=null || team.getImgUploadTime()!=null)
            deleteTeamImage(team);

        if(file.isEmpty()){
            if(fileName == null || !fileName.equals("default")){
                throw new BadRequestException("팀 이미지가 정상적으로 전달되지 않았습니다.");
            }
            // 1. file이 존재하지 않고, fileName이 "default"로 넘어옴 -> 기본 이미지 사용
            else if(fileName.equals("default")) {
                log.info("[INFO] uploadTeamImage : 기본이미지 사용");
                // 팀 이미지 삭제
                deleteTeamImage(team);
                team.uploadImage(null, null);
                return "기본 이미지";
            }
        }

        long imgUploadTime = Timestamp.valueOf(LocalDateTime.now()).getTime();
        String uploadFileName = "team" + team.getTeamIdx().toString() + imgUploadTime;

        log.info(uploadFileName);

        String imageUrl = imageService.uploadFiles("team", uploadFileName, file);
        team.uploadImage(imageUrl, imgUploadTime);
        log.info("[INFO] uploadTeamImage : 팀 이미지 업로드 성공 - "+ imageUrl);
        return imageUrl;
    }

    public void deleteTeamImage(Team team){

        if(team.getImgUrl() == null){
            log.info("[INFO] deleteTeamImage : 삭제 할 이미지가 존재하지 않음");
        } else {
            String deleteFileName = "team" + team.getTeamIdx().toString() + team.getImgUploadTime().toString();
            imageService.deleteFiles(deleteFileName);
            log.info("[INFO] deleteTeamImage : firebase 팀 이미지 삭제 - " + "team" + team.getTeamIdx().toString());
        }
    }

    @Transactional
    public String createTeam(User leader, TeamRequest.createReq req,
                           String fileName, MultipartFile file) throws IOException, FirebaseAuthException {

        Team createTeam = Team.builder()
                .leader(leader)
                .title(req.getTitle())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .isEmpty(true)
                .build();
        Team team = teamRepository.save(createTeam);

        String teamImageInfo = uploadTeamImage(team, fileName, file);

        Belong belong = Belong.builder()
                .user(leader)
                .team(team)
                .build();
        belongRepository.save(belong);

        log.info("[INFO] createTeam : 팀 생성 성공, " + team.getTeamIdx() + ", img : " + teamImageInfo);
        return "" + team.getTeamIdx() + ", " + teamImageInfo;

    }

    @Transactional
    public Long addMember(Long teamIdx, TeamRequest.memberEmailReq req, User inviter, User invitedUser) {

        // 해당 팀 IDX 가 존재하지 않는 경우에 대한 예외 처리
        Team team = teamRepository.findById(teamIdx)
                .orElseThrow(() -> {
                    log.info("[REJECT] addMember : 요청한 팀이 존재하지 않음");
                    return new NotFoundException("요청한 팀이 존재하지 않습니다.");
                });

        Long count = belongRepository.countMemberByTeam(teamIdx);

        User leader = team.getLeader();
        log.info("[INFO] addMember : 요청 팀의 리더 -> "+leader.getUserIdx());
        log.info("[INFO] addMember : 초대 요청자 -> " + inviter.getUserIdx());
        log.info("[INFO] addMember : 초대 대상자 -> " + invitedUser.getUserIdx());


        if(checkSameUser(leader, inviter)){
            //생성하려는 사용자와 팀짱이 동일
            log.info("[INFO] addMember : 요청 사용자가 팀의 팀짱과 일치함");

            // find invitedUser (존재하는 사용자인지 확인)
            if(invitedUser != null){

                log.info("[INFO] addMember : 서비스에 가입된 유저임 확인, IDX:"+invitedUser.getUserIdx());

                // 이미 팀에 존재하는 유저인지 확인
                Belong already = belongRepository.findUserByIndex(teamIdx, invitedUser.getUserIdx());

                if(already == null){

                    if(count >= 10) {
                        log.info("[REJECT] addMember : 초대 가능 인원(10명)을 초과함");
                        throw new BadRequestException("초대 가능 인원(10명)을 초과하였습니다.");

                    } else {
                        log.info("[INFO] addMember : 모든 요청에 유효함(팀짱, 초대 사용자 존재, 팀에 10명이 넘지 않음");

                        Belong belong = Belong.builder()
                                .user(invitedUser)
                                .team(team)
                                .build();

                        belongRepository.save(belong);

                        //team에 유저가 1명 이상 -> isEmpty = false;
                        team.updateEmpty();
                        log.info("[INFO] addMember : 팀원 초대 성공, new member idx: "+invitedUser.getUserIdx());
                        return invitedUser.getUserIdx();
                    }
                } else {
                    log.info("[REJECT] addMember : 이미 팀에 초대된 팀원");
                    throw new BadRequestException("이미 초대된 팀원입니다.");
                }
            } else {
                log.info("[REJECT] addMember : 해당 이메일을 가진 유저가 존재하지 않음");
                throw new NotFoundException("초대하려는 팀원이 존재하지 않습니다.");
            }
        } else {
            //생성하려는 사용자와 팀짱이 동일하지 않음
            log.info("[REJECT] addMember : 초대 권한 없음. 팀장만 초대 가능");
            throw new ForbiddenException("권한 없음. 팀장만 초대가 가능합니다.");
        }
    }


    @Transactional
    public List<TeamResponse.getMemberList> getMembers(Long teamIdx){
        //TODO: leader가 제일 맨 앞 순위로 오게!!

        // 해당 팀 IDX 가 존재하지 않는 경우에 대한 예외 처리
        Team team = teamRepository.findById(teamIdx)
                .orElseThrow(() -> {
                    log.info("[REJECT] getMembers : 요청한 팀이 존재하지 않음");
                    return new NotFoundException("요청한 팀이 존재하지 않습니다.");
                });


        List<User> userList = belongRepository.findAllByTeamIdx(team.getTeamIdx());

        List<TeamResponse.getMemberList> memberList = userList.stream().map(user -> new TeamResponse.getMemberList(
                user.getUserIdx(),
                user.getName(),
                user.getEmail(),
                user.getImage()
        )).collect(Collectors.toList());

        List<TeamResponse.getMemberList> memberListOfTopLeader = new ArrayList<>();
        for(TeamResponse.getMemberList m : memberList){
            if(m.getUserIdx() == team.getLeader().getUserIdx()) {
                memberListOfTopLeader.add(m);
                memberList.remove(m);
                break;
            }
        }

        memberListOfTopLeader.addAll(memberList);

        log.info("[INFO] getMembers : 팀원 조회 성공");
        return memberListOfTopLeader;
    }

    @Transactional
    public TeamResponse.showMyTeamList getMyTeams(User user){

        List<Team> teams = belongRepository.findAllByUserIdx(user.getUserIdx());

        List<TeamResponse.getMyTeams> teamList = teams.stream().map(team -> new TeamResponse.getMyTeams(
                team.getTeamIdx(),
                team.getTitle(),
                team.getStartDate(),
                team.getEndDate(),
                team.getImgUrl()
        )).collect(Collectors.toList());

//        if(teamList.isEmpty()){
//            throw new NotFoundException("속한 팀이 없습니다");
//        } else {
//            return teamList;
//        }

        TeamResponse.showMyTeamList showMyTeamLists = new TeamResponse.showMyTeamList(
                user.getName(),
                teamList
        );

        log.info("[INFO] getMyTeams : 나의 팀 조회 성공");
        return showMyTeamLists;
    }

    @Transactional
    public Long deleteTeam(Long teamIdx, User requester){

        // 해당 팀 IDX 가 존재하지 않는 경우에 대한 예외 처리
        Team team = teamRepository.findById(teamIdx)
                .orElseThrow(() -> {
                    log.info("[REJECT] getMembers : 요청한 팀이 존재하지 않음");
                    return new NotFoundException("요청한 팀이 존재하지 않습니다.");
                });

        //팀짱인지 확인 과정 필요
        User leader = team.getLeader();

        if(checkSameUser(leader, requester)){
            //요청 사용자와 팀짱이 동일
            log.info("[INFO] deleteTeam : 요청 사용자가 팀의 팀짱과 일치함");

            // 연결된 PLAN 모두 끊기
            planService.deleteAllPlan(team.getTeamIdx());
            log.info("[INFO] deleteTeam : 팀과 연결된 Plan 모두 삭제");

            // 연결된 archive 모두 끊기
            log.info("[INFO] deleteByTeamIdx : called");
//            List<Archive> archives = archiveRepository.findByTeam(team);
//            if(archives.isEmpty()) {
//                log.info("[INFO] deleteByTeamIdx : No archives to delete");
//            }
//            else {
//                archiveRepository.deleteByTeam(team);
//            }

            archiveService.deleteByTeamIdx(team);
            log.info("[INFO] deleteTeam : 팀과 연결된 Archive 모두 삭제");


            // 팀 삭제
            teamRepository.deleteByTeamIdx(teamIdx);
            log.info("[INFO] deleteTeam : 팀 삭제 성공, deleted team idx: "+teamIdx);

            // 팀 이미지 삭제(firebase)
            deleteTeamImage(team);

            return teamIdx;

        } else {
            //요청 사용자와 팀짱이 동일하지 않음
            log.info("[REJECT] deleteTeam : 삭제 권한 없음. 팀장만 팀 삭제 가능");
            throw new ForbiddenException("권한 없음. 팀장만 삭제가 가능합니다.");
        }
    }

    @Transactional
    public String deleteMember(Long teamIdx, TeamRequest.memberEmailReq req, User requester, User deletedUser){

        // 해당 팀 IDX 가 존재하지 않는 경우에 대한 예외 처리
        Team team = teamRepository.findById(teamIdx)
                .orElseThrow(() -> {
                    log.info("[REJECT] getMembers : 요청한 팀이 존재하지 않음");
                    return new NotFoundException("요청한 팀이 존재하지 않습니다.");
                });

        if(deletedUser==null){
            log.info("[REJECT] deleteMember : 해당 index를 가진 유저가 존재하지 않음");
            throw new BadRequestException("삭제하려는 팀원이 존재하지 않습니다.");
        }
        Belong isBelong = belongRepository.findUserByIndex(teamIdx, deletedUser.getUserIdx());
        if(isBelong==null){
            log.info("[REJECT] deleteMember : 유저가 팀원이 아님");
            throw new BadRequestException("해당 사용자는 팀 소속이 아닙니다.");
        }

        return deleteBelongMember(team, requester, deletedUser);
    }

    @Transactional
    public String updateTeam(Long teamIdx, TeamRequest.updateTeamReq req,
                           String fileName, MultipartFile file, User requester)
            throws IOException, FirebaseAuthException{

        // 해당 팀 IDX 가 존재하지 않는 경우에 대한 예외 처리
        Team team = teamRepository.findById(teamIdx)
                .orElseThrow(() -> {
                    log.info("[REJECT] getMembers : 요청한 팀이 존재하지 않음");
                    return new NotFoundException("요청한 팀이 존재하지 않습니다.");
                });

        //팀짱인지 확인 필요
        User leader = team.getLeader();

        if(checkSameUser(leader, requester)){
            //요청 사용자와 팀짱이 동일
            log.info("[INFO] updateTeam : 요청 사용자가 팀의 팀짱과 일치함(권한ㅇ)");

            //팀 이미지 변경 확인
            if(file.isEmpty() && (fileName == null || fileName.isEmpty() || fileName.equals(""))){
                log.info("[INFO] uploadTeamImage : 기존 이미지 유지");
            } else {
                log.info("[INFO] uploadTeamImage : 팀 이미지 수정");
                uploadTeamImage(team, fileName, file);
            }

            // 팀 정보 업데이트
            team.updateTeam(req.getTitle(), req.getStartDate(), req.getEndDate());
            log.info("[INFO] updateTeam : 팀 정보 업데이트 성공, updated team idx: "+teamIdx);
            return team.getImgUrl();

        } else {
            //요청 사용자와 팀짱이 동일하지 않음
            log.info("[REJECT] updateTeam : 수정 권한 없음. 팀장만 수정 가능");
            throw new ForbiddenException("권한 없음. 팀장만 수정 가능합니다.");
        }
    }

    //------------팀원 삭제 로직(팀원 삭제, 유저 탈퇴에서 사용)---------------//
    @Transactional
    public String deleteBelongMember(Team team, User requester, User deleteMember){
        log.info("[API] deleteBelongMember : 팀원 삭제(공통로직)");
        User leader = team.getLeader();

        // 1. 팀짱이라면
        if(checkSameUser(leader, requester)) {
            //1-1. 삭제하려는 팀원이 나(팀짱)라면
            if(checkSameUser(deleteMember, requester)) {
                log.info("[INFO] deleteBelongMember : 팀짱이 팀 나가기 시도");
                //-> 다른 팀원한테 넘기고 나(팀짱)는 나가기
                List<User> teamUsers = belongRepository.findAllByTeamIdx(team.getTeamIdx());

                // 다른 팀원이 존재하지 않는다면
                if(teamUsers.size() == 1 && checkSameUser(teamUsers.get(0), deleteMember)){

                    // 연결된 PLAN 모두 끊기
                    planService.deleteAllPlan(team.getTeamIdx());
                    log.info("[INFO] deleteTeam : 팀과 연결된 Plan 모두 삭제");

                    // 연결된 archive 모두 끊기
                    archiveService.deleteByTeamIdx(team);
                    log.info("[INFO] deleteTeam : 팀과 연결된 Archive 모두 삭제");



                    // 팀 삭제
                    teamRepository.delete(team);

                    // 연결된 PLAN 모두 끊기
                    planService.deleteAllPlan(team.getTeamIdx());
                    log.info("[INFO] deleteTeam : 팀과 연결된 Plan 모두 삭제");

                    // 연결된 archive 모두 끊기
                    archiveService.deleteByTeamIdx(team);
                    log.info("[INFO] deleteTeam : 팀과 연결된 Archive 모두 삭제");

                    log.info("[INFO] deleteBelongMember : 팀에 더이상 팀원이 존재하지 않음, 팀 삭제");

                    // 팀 이미지 삭제(firebase)
                    deleteTeamImage(team);

                    return "더 이상 팀원이 존재하지 않음, 팀 삭제";

                } else {
                    User randomUser;

                    do {
                        Collections.shuffle(teamUsers);
                        randomUser = teamUsers.get(0);
                    } while (checkSameUser(randomUser, requester));

                    //System.out.println("random user idx 1 : " + teamUsers.get(0).getEmail());
                    //System.out.println("random user idx 2 : " + teamUsers.get(1).getEmail());
                    //System.out.println("randomUser : " + randomUser.getEmail());

                    // 랜덤한 팀원한테 리더 넘기고 나는 belong 삭제
                    team.changeLeader(randomUser);
                    log.info("[INFO] deleteBelongMember : 팀 내 랜덤한 유저에게 팀짱 부여");

                    Belong deleteUser = belongRepository.findUserByIndex(team.getTeamIdx(), requester.getUserIdx());
                    belongRepository.deleteById(deleteUser.getBelongIdx());
                    log.info("[INFO] deleteBelongMember : 팀짱 나가기 성공, 새로운 팀짱 idx: "+randomUser.getUserIdx());
                    return "팀장 변경, " + randomUser.getUserIdx();
                }

            } else {
                //1-2. 삭제하려는 팀원이 내가 아니라면 == 팀원 삭제
                Belong deleteUser = belongRepository.findUserByIndex(team.getTeamIdx(), deleteMember.getUserIdx());
                belongRepository.deleteById(deleteUser.getBelongIdx());
                log.info("[INFO] deleteBelongMember : 팀원 삭제 성공, deleted member idx: "+deleteMember.getUserIdx());
                return "팀원 삭제, " + deleteMember.getUserIdx();
            }
        } else {
            //2. 팀짱이 아니라면
            //2-1. 삭제하려는 팀원이 나라면
            if(checkSameUser(deleteMember, requester)){
                //-> 그냥 나 나가기
                Belong deleteUser = belongRepository.findUserByIndex(team.getTeamIdx(), requester.getUserIdx());
                belongRepository.deleteById(deleteUser.getBelongIdx());
                log.info("[INFO] deletedBelongMember : 팀 나가기 성공, deleted member idx: "+ requester.getUserIdx());
                return "팀 나가기, " + requester.getUserIdx();
            } else {
                //2-2. 삭제하려는 팀원이 내가 아니라면 -> 권한 없음
                log.info("[REJECT] deleteBelongMember : 내보내기 권한 없음, 팀짱만 팀원 내보내기 가능");
                throw new ForbiddenException("권한 없음. 팀장만 팀원 삭제가 가능합니다.");
            }
        }
    }

    //------------기존 userService 내 코드 (유저 탈퇴용)---------------//

    //team 삭제시 plan, archive 전부 삭제 => teamIdx
    @Transactional
    public void deleteTeamByLeaderIdx(Long userIdx){
        List<Team> teamList = teamRepository.findALLByLeaderIdx(userIdx).orElseThrow(()->new BadRequestException("team이 등록되어있지 않은 user"));
        for(Team team : teamList){
            System.out.println("deleteTeam : "+team.getTeamIdx());

//            userService.deleteArchiveByTeamIdx(team.getTeamIdx());
//            userService.deletePlanByTeamIdx(team.getTeamIdx());
//            archiveService.deleteArchiveByTeamIdx(team.getTeamIdx());
//            planService.deletePlanByTeamIdx(team.getTeamIdx());

            teamRepository.delete(team);
        }
        //System.out.println("팀삭제 끝!");
    }


    //------------새로 만든 delete 코드 (유저 탈퇴용)---------------//

    //team 삭제시 plan, archive 전부 삭제 => teamIdx
    @Transactional
    public void deleteBelongTeam(User user){
        log.info("[API] deleteBelongTeam : 사용자가 속한 팀 정보 모두 삭제(유저탈퇴접근)");

        //belong 에서 내가 속한 팀을 찾고, 그 팀의 teamIdx 로 접근해서 내가 리더인지 아닌지 확인
        List<Team> belongTeamList = belongRepository.findAllByUserIdx(user.getUserIdx());

        for(Team team : belongTeamList){
            deleteBelongMember(team, user, user);
            // 유저 탈퇴 시에는 유저의 belong 을 모두 지우고 archive랑 plan은 팀이 지워질 때에만 삭제하는걸로
//            userService.deleteArchiveByTeamIdx(team.getTeamIdx()); //archiveService.delete...
//            userService.deletePlanByTeamIdx(team.getTeamIdx()); //planService.delete...

        }
        log.info("[INFO] deleteBelongTeam : 사용자가 속한 팀 정보 모두 삭제 완료");
    }

}