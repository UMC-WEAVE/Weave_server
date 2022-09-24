package com.weave.weaveserver.service;

import com.google.api.Http;
import com.weave.weaveserver.config.exception.BadRequestException;
import com.weave.weaveserver.config.exception.EntityNotFoundException;
import com.weave.weaveserver.config.exception.ForbiddenException;
import com.weave.weaveserver.config.exception.NotFoundException;
import com.weave.weaveserver.config.jwt.TokenService;
import com.weave.weaveserver.domain.*;
import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.dto.TeamRequest;
import com.weave.weaveserver.dto.TeamResponse;
import com.weave.weaveserver.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private ImageService imageService;

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final BelongRepository belongRepository;

    private final PlanRepository planRepository;

    private final ArchiveRepository archiveRepository;

    private UserService userService;

    //private PlanService planService;
    //private ArchiveService archiveService;


    @Transactional
    public Long createTeam(HttpServletRequest httpServletRequest, TeamRequest.createReq req,
                                                   String fileName, MultipartFile file) throws IOException{
        log.info("[API] createTeam - POST /teams/create, 팀 생성");
        //startDate vs endDate
//        LocalDate startDate = req.getStartDate();
//        LocalDate endDate = req.getEndDate();
//        if(startDate.compareTo(endDate) < 0){
//            return ResponseEntity.ok(new JsonResponse(2005, "여행 시작 날짜와 끝나는 날짜를 다시 확인해주세요",null));
//        }

        String userEmail = tokenService.getUserEmail(httpServletRequest);
        User leader = userRepository.findUserByEmail(userEmail);

        //---System.out.println(leader.getUserIdx());

        String imgUrl = "";

        if(fileName == null || file == null) {
            //System.out.println("파일 없음");
            log.info("[INFO] createTeam - 팀 이미지를 업로드하지 않음(기본이미지 사용)");
            imgUrl = "null";
        } else {
            imgUrl = imageService.uploadToStorage("team", fileName, file);
        }
        Team team = Team.builder()
                .leader(leader)
                .title(req.getTitle())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .imgUrl(imgUrl)
                .isEmpty(true)
                .build();
        Long teamIdx = teamRepository.save(team).getTeamIdx();

        Belong belong = Belong.builder()
                .user(leader)
                .team(team)
                .build();
        belongRepository.save(belong);

        log.info("[INFO] createTeam - 팀 생성 성공, create team idx: ", teamIdx);
        return teamIdx;

    }

    @Transactional
    public Long addMember(Long teamIdx, TeamRequest.addMemberReq req, HttpServletRequest httpServletRequest) {
        log.info("[API] addMember - POST /teams/{teamIdx}/invite, 팀원 초대");

        // 해당 팀 IDX 가 존재하지 않는 경우에 대한 예외 처리
        Team team = teamRepository.findById(teamIdx)
                .orElseThrow(() -> {
                    log.error("[ERROR] addMember - 해당 팀이 존재하지 않음");
                    return new NotFoundException("해당 팀이 존재하지 않습니다.");
                });

        Long count = belongRepository.countMemberByTeam(teamIdx);
        //---System.out.println(count);

        User leader = team.getLeader();

        String userEmail = tokenService.getUserEmail(httpServletRequest);
        User creator = userRepository.findUserByEmail(userEmail);

        if(leader.equals(creator)){
            //생성하려는 사용자와 팀짱이 동일
            //System.out.println("same");
            log.info("[INFO] addMember - 요청 사용자가 팀의 팀짱과 일치함");

            // find user (존재하는 사용자인지 확인)
            User invitedUser = userRepository.findUserByEmail(req.getEmail());
            if(invitedUser != null){
                //System.out.println("초대 팀원 존재 : "+ invitedUser.getEmail());
                log.info("[INFO] addMember - 서비스에 가입된 유저임 확인, IDX:"+invitedUser.getUserIdx());

                // 이미 팀에 존재하는 유저인지 확인
                Belong already = belongRepository.findUserByIndex(teamIdx, invitedUser.getUserIdx());

                if(already == null){

                    if(count >= 10) {
                        //System.out.println("초대 가능한 인원을 초과했습니다");
                        log.error("[ERROR] addMember - 초대 가능 인원(10명)을 초과함");
                        throw new BadRequestException("초대 가능 인원을 초과하였습니다.");

                    } else {
                        //System.out.println("팀짱이고 사용자도 존재하고, 팀에도 없고 10명 이내에요~!!");
                        log.info("[INFO] addMember - 모든 요청에 유효함(팀짱, 초대 사용자 존재, 팀에 10명이 넘지 않음");
                        Long userIdx = invitedUser.getUserIdx();
                        User user = userRepository.getReferenceById(userIdx);

                        Belong belong = Belong.builder()
                                .user(user)
                                .team(team)
                                .build();

                        belongRepository.save(belong);

                        //team에 유저가 1명 이상 -> isEmpty = false;
                        team.updateEmpty();
                        log.info("[INFO] addMember - 팀원 초대 성공, new member idx: "+userIdx);
                        return userIdx;
                    }
                } else {
                    //System.out.println("이미 초대된 팀원");
                    log.error("[ERROR] addMember - 이미 팀에 초대된 팀원");
                    throw new BadRequestException("이미 초대된 팀원입니다.");
                }
            } else {
                //System.out.println("초대 팀원이 존재하지 않음");
                log.error("[ERROR] addMember - 해당 이메일을 가진 유저가 존재하지 않음");
                throw new NotFoundException("초대하려는 팀원이 존재하지 않습니다.");
            }
        } else {
            //생성하려는 사용자와 팀짱이 동일하지 않음
            //System.out.println("not same");
            log.error("[ERROR] addMember - 초대 권한 없음. 팀장만 초대 가능");
            throw new ForbiddenException("권한 없음. 팀장만 초대가 가능합니다.");
        }
    }


    @Transactional
    public List<TeamResponse.getMemberList> getMembers(Long teamIdx){
        log.info("[API] getMembers - GET /teams/{teamIdx}/members, 팀원 조회");

        // 해당 팀 IDX 가 존재하지 않는 경우에 대한 예외 처리
        Team team = teamRepository.findById(teamIdx)
                .orElseThrow(() -> {
                    log.error("[ERROR] getMembers - 해당 팀이 존재하지 않음");
                    return new NotFoundException("해당 팀이 존재하지 않습니다.");
                });

        List<User> userList = belongRepository.findAllByTeamIdx(team.getTeamIdx());

        List<TeamResponse.getMemberList> memberList = userList.stream().map(user -> new TeamResponse.getMemberList(
                user.getUserIdx(),
                user.getName(),
                user.getImage()
        )).collect(Collectors.toList());
        log.info("[INFO] getMembers - 팀원 조회 성공");
        return memberList;
    }

    @Transactional
    public List<TeamResponse.getMyTeams> getMyTeams(HttpServletRequest httpServletRequest){
        log.info("[API] getMyTeams - GET /teams, 나의 팀 조회");

        String userEmail = tokenService.getUserEmail(httpServletRequest);
        User user = userRepository.findUserByEmail(userEmail);

        List<Team> teams = belongRepository.findAllByUserIdx(user.getUserIdx());

        List<TeamResponse.getMyTeams> teamList = teams.stream().map(team -> new TeamResponse.getMyTeams(
                user.getName(),
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

        log.info("[INFO] getMyTeams - 나의 팀 조회 성공");
        return teamList;
    }

    @Transactional
    public Long deleteTeam(Long teamIdx, HttpServletRequest httpServletRequest){
        log.info("[API] deleteTeam - DELETE /teams/{teamIdx}, 팀 삭제");

        // 해당 팀 IDX 가 존재하지 않는 경우에 대한 예외 처리
        Team team = teamRepository.findById(teamIdx)
                .orElseThrow(() -> {
                    log.error("[ERROR] deleteTeam - 해당 팀이 존재하지 않음");
                    return new NotFoundException("해당 팀이 존재하지 않습니다.");
                });

        //팀짱인지 확인 과정 필요
        User leader = team.getLeader();

        String userEmail = tokenService.getUserEmail(httpServletRequest);
        User requester = userRepository.findUserByEmail(userEmail);

        if(leader.equals(requester)){
            //요청 사용자와 팀짱이 동일
            //System.out.println("same");
            log.info("[INFO] deleteTeam - 요청 사용자가 팀의 팀짱과 일치함");

            // 연결된 PLAN 모두 끊기
            List<Plan> plans = planRepository.findAllByTeamIdx(teamIdx);
            for(Plan plan : plans){
                planRepository.deleteById(plan.getPlanIdx());
                //System.out.println("Plan 삭제");
                log.info("[INFO] deleteTeam - 팀과 연결된 Plan 모두 삭제");
            }

            // 연결된 archive 모두 끊기
            List<Archive> archives = archiveRepository.findByTeam(team);
            for(Archive archive : archives){
                archiveRepository.deleteByArchiveIdx(archive.getArchiveIdx());
                //System.out.println("Archive 삭제");
                log.info("[INFO] deleteTeam - 팀과 연결된 Archive 모두 삭제");
            }

            // 팀 삭제
            teamRepository.deleteByTeamIdx(teamIdx);
            log.info("[INFO] deleteTeam - 팀 삭제 성공, deleted team idx: "+teamIdx);
            return teamIdx;

        } else {
            //요청 사용자와 팀짱이 동일하지 않음
            //System.out.println("not same");
            log.error("[ERROR] deleteTeam - 삭제 권한 없음. 팀장만 팀 삭제 가능");
            throw new ForbiddenException("권한 없음. 팀장만 삭제가 가능합니다.");
        }
    }

    @Transactional
    public String deleteMember(Long teamIdx, TeamRequest.deleteMemberReq req, HttpServletRequest httpServletRequest){
        log.info("[API] deleteMember - DELETE /teams/{teamIdx}/members, 팀원 삭제");

        // 해당 팀 IDX 가 존재하지 않는 경우에 대한 예외 처리
        Team team = teamRepository.findById(teamIdx)
                .orElseThrow(() -> {
                    log.error("[ERROR] deleteMember - 해당 팀이 존재하지 않음");
                    return new NotFoundException("해당 팀이 존재하지 않습니다.");
                });

        User leader = team.getLeader();

        String userEmail = tokenService.getUserEmail(httpServletRequest);
        User requester = userRepository.findUserByEmail(userEmail); //삭제 요청자
        User user = userRepository.getReferenceById(req.getUserIdx()); //삭제하려는 사용자

        if(user==null){
            //System.out.println("존재하지 않는 사용자");
            log.error("[ERROR] deleteMember - 해당 index를 가진 유저가 존재하지 않음");
            throw new BadRequestException("삭제하려는 팀원이 존재하지 않습니다.");
        }
        Belong isBelong = belongRepository.findUserByIndex(teamIdx, req.getUserIdx());
        if(isBelong==null){
            //System.out.println("팀 소속 유저가 아님");
            log.error("[ERROR] deleteMember - 유저가 팀원이 아님");
            throw new BadRequestException("해당 사용자는 팀 소속이 아닙니다.");
        }

        return deleteBelongMember(team, requester, user);
    }

    @Transactional
    public Long updateTeam(Long teamIdx, TeamRequest.updateTeamReq req,
                                                   String fileName, MultipartFile file, HttpServletRequest httpServletRequest)
    throws IOException{
        log.info("[API] updateTeam - PATCH /teams/{teamIdx}/modify, 팀 정보 수정");

        // 해당 팀 IDX 가 존재하지 않는 경우에 대한 예외 처리
        Team team = teamRepository.findById(teamIdx)
                .orElseThrow(() -> {
                    log.error("[ERROR] updateTeam - 해당 팀이 존재하지 않음");
                    return new NotFoundException("해당 팀이 존재하지 않습니다.");
                });

        //팀짱인지 확인 필요
        User leader = team.getLeader();

        String userEmail = tokenService.getUserEmail(httpServletRequest);
        User requester = userRepository.findUserByEmail(userEmail);

        if(leader.equals(requester)){
            //요청 사용자와 팀짱이 동일
            //System.out.println("same");
            log.info("[INFO] updateTeam - 요청 사용자가 팀의 팀짱과 일치함(권한O)");

            // 지금 DB에 있는 fileName 과 동일한지 확인 필요
            String imgUrl = team.getImgUrl();


            //null 로 보낼 경우 -> 이미지 삭제 처리
            if(fileName == null || file == null){
                //System.out.println("파일 삭제");
                log.info("[INFO] updateTeam - 팀 이미지 삭제(기본이미지로 변경)");
                imgUrl = "null";
            } else {

                if(imgUrl != null){
                    // image 가 null 이 아닐 때 저장된 경로에서 fileName 을 찾아냄
                    String existFile = imgUrl.replaceAll("https://storage.googleapis.com/weave_bucket/", "");
                    //System.out.println(existFile);

                    // 만약 넘어온 fileName 과 기존의 fileName 이 동일하다면 같은 이미지(업데이트 X) 로 판단
                    // 동일하지 않다면 새로운 이미지(업데이트 O) 로 판단
                    if(existFile.equals(fileName)) {
                        //System.out.println("이미지 업데이트 없음");
                        log.info("[INFO] updateTeam - 기존 팀 이미지와 동일, 이미지 업데이트 없음");
                    } else {
                        //System.out.println("이미지 재 업로드!");
                        log.info("[INFO] updateTeam - 기존 팀 이미지와 다름, 이미지 업데이트");
                        imgUrl = imageService.uploadToStorage("team", fileName, file);
                    }
                } else {
                    // image 필드가 null 일 경우 비교 없이 바로 업로드
                    //System.out.println("이미지 새 업로드!");
                    log.info("[INFO] updateTeam - 팀 이미지 업로드");
                    imgUrl = imageService.uploadToStorage("team", fileName, file);
                }

            }

            //업데이트
            team.updateTeam(req.getTitle(), req.getStartDate(), req.getEndDate(), imgUrl);
            log.info("[INFO] updateTeam - 팀 정보 업데이트 성공, updated team idx: "+teamIdx);
            return teamIdx;

        } else {
            //요청 사용자와 팀짱이 동일하지 않음
            //System.out.println("not same");
            log.error("[ERROR] updateTeam - 수정 권한 없음. 팀장만 수정 가능");
            throw new ForbiddenException("권한 없음. 팀장만 수정 가능합니다.");
        }
    }

    //------------팀원 삭제 로직(팀원 삭제, 유저 탈퇴에서 사용)---------------//
    @Transactional
    public String deleteBelongMember(Team team, User requester, User deleteMember){
        log.info("[API] deleteBelongMember");
        User leader = team.getLeader();
        //System.out.println("team leader idx : "+leader.getUserIdx());
        // 1. 팀짱이라면
        if(leader.equals(requester)) {
        //1-1. 삭제하려는 팀원이 나(팀짱)라면
            if(deleteMember.equals(requester)) {
                log.info("[INFO] deleteBelongMember - 팀짱이 팀 나가기 시도");
                //-> 다른 팀원한테 넘기고 나(팀짱)는 나가기
                List<User> teamUsers = belongRepository.findAllByTeamIdx(team.getTeamIdx());

                //System.out.println("team user size : "+teamUsers.size());
                //System.out.println("last member : "+teamUsers.get(0).getUserIdx());

                // 다른 팀원이 존재하지 않는다면
                if(teamUsers.size() == 1 && teamUsers.get(0).equals(deleteMember)){
                    //delete Team
                    teamRepository.delete(team);
                    log.info("[INFO] deleteBelongMember - 팀에 더이상 팀원이 존재하지 않음, 팀 삭제");
                    return "더 이상 팀원이 존재하지 않음, 팀 삭제";

                } else {

                    User randomUser = teamUsers.get(1);

                    do {
                        Collections.shuffle(teamUsers);
                        randomUser = teamUsers.get(0);
                    } while (randomUser.equals(requester));

                    //System.out.println("random user idx 1 : " + teamUsers.get(0).getEmail());
                    //System.out.println("random user idx 2 : " + teamUsers.get(1).getEmail());
                    //System.out.println("randomUser : " + randomUser.getEmail());

                    // 랜덤한 팀원한테 리더 넘기고 나는 belong 삭제
                    team.changeLeader(randomUser);
                    //System.out.println("update team leader");
                    log.info("[INFO] deleteBelongMember - 팀 내 랜덤한 유저에게 팀짱 부여");

                    Belong deleteUser = belongRepository.findUserByIndex(team.getTeamIdx(), requester.getUserIdx());
                    belongRepository.deleteById(deleteUser.getBelongIdx());
                    log.info("[INFO] deleteBelongMember - 팀짱 나가기 성공, 새로운 팀짱 idx: "+randomUser.getUserIdx());
                    return "팀장 변경, " + randomUser.getUserIdx();
                }

            } else {
                //1-2. 삭제하려는 팀원이 내가 아니라면 == 팀원 삭제
                Belong deleteUser = belongRepository.findUserByIndex(team.getTeamIdx(), deleteMember.getUserIdx());
                belongRepository.deleteById(deleteUser.getBelongIdx());
                log.info("[INFO] deleteBelongMember - 팀원 삭제 성공, deleted member idx: "+deleteMember.getUserIdx());
                return "팀원 삭제, " + deleteMember.getUserIdx();
            }
        } else {
        //2. 팀짱이 아니라면
        //2-1. 삭제하려는 팀원이 나라면
            if(deleteMember.equals(requester)){
                //-> 그냥 나 나가기
                Belong deleteUser = belongRepository.findUserByIndex(team.getTeamIdx(), requester.getUserIdx());
                belongRepository.deleteById(deleteUser.getBelongIdx());
                log.info("[INFO] deletedBelongMember - 팀 나가기 성공, deleted member idx: "+ requester.getUserIdx());
                return "팀 나가기, " + requester.getUserIdx();
            } else {
        //2-2. 삭제하려는 팀원이 내가 아니라면 -> 권한 없음
                log.error("[ERROR] deleteBelongMember - 내보내기 권한 없음, 팀짱만 팀원 내보내기 가능");
                throw new ForbiddenException("권한 없음. 팀장만 팀원 삭제가 가능합니다.");
            }
        }
    }

    //------------기존 userService 내 코드---------------//

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
        System.out.println("팀삭제 끝!");
    }


    //------------새로 만든 delete 코드---------------//

    //team 삭제시 plan, archive 전부 삭제 => teamIdx
    @Transactional
    public void deleteBelongTeam(String userEmail){
        log.info("[API] deleteBelongTeam");
//        String userEmail = tokenService.getUserEmail(httpServletRequest);
        User user = userRepository.findUserByEmail(userEmail);

        //belong 에서 내가 속한 팀을 찾고, 그 팀의 teamIdx 로 접근해서 내가 리더인지 아닌지 확인
        List<Team> belongTeamList = belongRepository.findAllByUserIdx(user.getUserIdx());

        for(Team team : belongTeamList){
            deleteBelongMember(team, user, user);
//            userService.deleteArchiveByTeamIdx(team.getTeamIdx()); //archiveService.delete...
//            userService.deletePlanByTeamIdx(team.getTeamIdx()); //planService.delete...

        }
        log.info("[INFO] deleteBelongTeam - 팀 삭제 성공");
        System.out.println("팀삭제 끝!");
    }
}
