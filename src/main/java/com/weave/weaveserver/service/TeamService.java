package com.weave.weaveserver.service;

import com.weave.weaveserver.config.exception.NotFoundException;
import com.weave.weaveserver.config.jwt.TokenService;
import com.weave.weaveserver.domain.*;
import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.dto.TeamRequest;
import com.weave.weaveserver.dto.TeamResponse;
import com.weave.weaveserver.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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


    @Transactional
    public ResponseEntity<JsonResponse> createTeam(HttpServletRequest httpServletRequest, TeamRequest.createReq req,
                                                   String fileName, MultipartFile file) throws IOException{

        //startDate vs endDate
//        LocalDate startDate = req.getStartDate();
//        LocalDate endDate = req.getEndDate();
//        if(startDate.compareTo(endDate) < 0){
//            return ResponseEntity.ok(new JsonResponse(2005, "여행 시작 날짜와 끝나는 날짜를 다시 확인해주세요",null));
//        }

        String userEmail = tokenService.getUserEmail(httpServletRequest);
        User leader = userRepository.findUserByEmail(userEmail);

        System.out.println(leader.getUserIdx());

        String imgUrl = "";

        if(fileName == null || file == null) {
            System.out.println("파일 없음");
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

        return ResponseEntity.ok(new JsonResponse(200, "Success, createTeam",teamIdx));

    }

    @Transactional
    public ResponseEntity<JsonResponse> addMember(Long teamIdx, TeamRequest.addMemberReq req, HttpServletRequest httpServletRequest) {

        // 해당 팀 IDX 가 존재하지 않는 경우에 대한 예외 처리
        Team team = teamRepository.findById(teamIdx)
                .orElseThrow(() -> new NotFoundException("해당 팀이 존재하지 않습니다."));

        Long count = belongRepository.countMemberByTeam(teamIdx);
        System.out.println(count);

        User leader = team.getLeader();

        String userEmail = tokenService.getUserEmail(httpServletRequest);
        User creator = userRepository.findUserByEmail(userEmail);

        if(leader.equals(creator)){
            //생성하려는 사용자와 팀짱이 동일
            System.out.println("same");

            // find user (존재하는 사용자인지 확인)
            User invitedUser = userRepository.findUserByEmail(req.getEmail());
            if(invitedUser != null){
                System.out.println("초대 팀원 존재 : "+ invitedUser.getEmail());

                // 이미 팀에 존재하는 유저인지 확인
                Belong already = belongRepository.findUserByIndex(teamIdx, invitedUser.getUserIdx());

                if(already == null){

                    if(count >= 10) {
                        System.out.println("초대 가능한 인원을 초과했습니다");
                        return ResponseEntity.ok(new JsonResponse(2005, "초대 가능 인원을 초과하였습니다", null));

                    } else {
                        System.out.println("팀짱이고 사용자도 존재하고, 팀에도 없고 10명 이내에요~!!");

                        Long userIdx = invitedUser.getUserIdx();
                        User user = userRepository.getReferenceById(userIdx);

                        Belong belong = Belong.builder()
                                .user(user)
                                .team(team)
                                .build();

                        belongRepository.save(belong);

                        //team에 유저가 1명 이상 -> isEmpty = false;
                        team.updateEmpty();

                        return ResponseEntity.ok(new JsonResponse(200, "Success", userIdx));
                    }
                } else {
                    System.out.println("이미 초대된 팀원");
                    return ResponseEntity.ok(new JsonResponse(2002, "이미 속한 팀원입니다.", null));
                }
            } else {
                System.out.println("초대 팀원이 존재하지 않음");
                return ResponseEntity.ok(new JsonResponse(2001, "해당 사용자가 존재하지 않습니다.", null));
            }
        } else {
            //생성하려는 사용자와 팀짱이 동일하지 않음
            System.out.println("not same");
            return ResponseEntity.ok(new JsonResponse(2000, "권한이 없습니다. 팀장만 가능합니다.", null));
        }
    }


    @Transactional
    public List<TeamResponse.getMemberList> getMembers(Long teamIdx){

        // 해당 팀 IDX 가 존재하지 않는 경우에 대한 예외 처리
        Team team = teamRepository.findById(teamIdx)
                .orElseThrow(() -> new NotFoundException("해당 팀이 존재하지 않습니다."));

        List<User> userList = belongRepository.findAllByTeamIdx(team.getTeamIdx());

        List<TeamResponse.getMemberList> memberList = userList.stream().map(user -> new TeamResponse.getMemberList(
                user.getUserIdx(),
                user.getName(),
                user.getImage()
        )).collect(Collectors.toList());

        return memberList;
    }

    @Transactional
    public ResponseEntity<JsonResponse> getMyTeams(HttpServletRequest httpServletRequest){

        String userEmail = tokenService.getUserEmail(httpServletRequest);
        User user = userRepository.findUserByEmail(userEmail);

        List<Team> teams = belongRepository.findAllByUserIdx(user.getUserIdx());

        List<TeamResponse.teamResponse> teamList = teams.stream().map(team -> new TeamResponse.teamResponse(
                team.getTeamIdx(),
                team.getTitle(),
                team.getStartDate(),
                team.getEndDate(),
                team.getImgUrl()
        )).collect(Collectors.toList());

        if(teamList.isEmpty()){
            return ResponseEntity.ok(new JsonResponse(2003, "속한 팀이 없습니다.", teamList));

        } else {
            return ResponseEntity.ok(new JsonResponse(200, "Success", teamList));
        }

    }

    @Transactional
    public ResponseEntity<JsonResponse> deleteTeam(Long teamIdx, HttpServletRequest httpServletRequest){

        // 해당 팀 IDX 가 존재하지 않는 경우에 대한 예외 처리
        Team team = teamRepository.findById(teamIdx)
                .orElseThrow(() -> new NotFoundException("해당 팀이 존재하지 않습니다."));

        //팀짱인지 확인 과정 필요
        User leader = team.getLeader();

        String userEmail = tokenService.getUserEmail(httpServletRequest);
        User requester = userRepository.findUserByEmail(userEmail);

        if(leader.equals(requester)){
            //요청 사용자와 팀짱이 동일
            System.out.println("same");

            // 연결된 PLAN 모두 끊기
            List<Plan> plans = planRepository.findAllByTeamIdx(teamIdx);
            for(int i=0; i<plans.size();i++){
                planRepository.deleteById(plans.get(i).getPlanIdx());
                System.out.println("Plan 삭제");
            }

            // 연결된 archive 모두 끊기
            List<Archive> archives = archiveRepository.findByTeam(team);
            for(int i=0; i<archives.size();i++){
                archiveRepository.deleteByArchiveIdx(archives.get(i).getArchiveIdx());
                System.out.println("Archive 삭제");
            }


            // 팀 삭제
            teamRepository.deleteByTeamIdx(teamIdx);

            return ResponseEntity.ok(new JsonResponse(200, "Success", teamIdx));

        } else {
            //요청 사용자와 팀짱이 동일하지 않음
            System.out.println("not same");
            return ResponseEntity.ok(new JsonResponse(2000, "권한이 없습니다. 팀장만 가능합니다.", null));
        }
    }

    @Transactional
    public ResponseEntity<JsonResponse> deleteMember(Long teamIdx, TeamRequest.deleteMemberReq req, HttpServletRequest httpServletRequest){

        // 해당 팀 IDX 가 존재하지 않는 경우에 대한 예외 처리
        Team team = teamRepository.findById(teamIdx)
                .orElseThrow(() -> new NotFoundException("해당 팀이 존재하지 않습니다."));

        User leader = team.getLeader();

        String userEmail = tokenService.getUserEmail(httpServletRequest);
        User requester = userRepository.findUserByEmail(userEmail);

        if(leader.equals(requester)){
            //요청 사용자와 팀짱이 동일
            System.out.println("same");

            // 삭제하려는 팀원이 존재하는 팀원이지 확인
            User user = userRepository.getReferenceById(req.getUserIdx());

            if(user != null){
                // 삭제하려는 팀원이 팀 소속인지 확인
                Belong isBelong = belongRepository.findUserByIndex(teamIdx, user.getUserIdx());

                if(isBelong != null){
                    // 소속이라면
                    Belong deleteUser = belongRepository.findUserByIndex(teamIdx, user.getUserIdx());
                    belongRepository.deleteById(deleteUser.getBelongIdx());
                    return ResponseEntity.ok(new JsonResponse(200, "Success", user.getUserIdx()));

                } else {
                    //소속이 아니라면
                    System.out.println("팀 소속 유저가 아님");
                    return ResponseEntity.ok(new JsonResponse(2001, "해당 사용자가 존재하지 않습니다.", null));
                }
            } else {
                System.out.println("존재하지 않는 사용자");
                return ResponseEntity.ok(new JsonResponse(2001, "해당 사용자가 존재하지 않습니다.", null));
            }

        } else {
            //요청 사용자와 팀짱이 동일하지 않음
            System.out.println("not same");
            return ResponseEntity.ok(new JsonResponse(2000, "권한이 없습니다. 팀장만 가능합니다.", null));
        }
    }

    @Transactional
    public ResponseEntity<JsonResponse> updateTeam(Long teamIdx, TeamRequest.updateTeamReq req,
                                                   String fileName, MultipartFile file, HttpServletRequest httpServletRequest)
    throws IOException{

        // 해당 팀 IDX 가 존재하지 않는 경우에 대한 예외 처리
        Team team = teamRepository.findById(teamIdx)
                .orElseThrow(() -> new NotFoundException("해당 팀이 존재하지 않습니다."));

        //팀짱인지 확인 필요
        User leader = team.getLeader();

        String userEmail = tokenService.getUserEmail(httpServletRequest);
        User requester = userRepository.findUserByEmail(userEmail);

        if(leader.equals(requester)){
            //요청 사용자와 팀짱이 동일
            System.out.println("same");

            // 지금 DB에 있는 fileName 과 동일한지 확인 필요
            String imgUrl = team.getImgUrl();


            //null 로 보낼 경우 -> 이미지 삭제 처리
            if(fileName == null || file == null){
                System.out.println("파일 삭제");
                imgUrl = "null";
            } else {

                if(imgUrl != null){
                    // image 가 null 이 아닐 때 저장된 경로에서 fileName 을 찾아냄
                    String existFile = imgUrl.replaceAll("https://storage.googleapis.com/weave_bucket/", "");
                    System.out.println(existFile);

                    // 만약 넘어온 fileName 과 기존의 fileName 이 동일하다면 같은 이미지(업데이트 X) 로 판단
                    // 동일하지 않다면 새로운 이미지(업데이트 O) 로 판단
                    if(existFile.equals(fileName)) {
                        System.out.println("이미지 업데이트 없음");
                    } else {
                        System.out.println("이미지 재 업로드!");
                        imgUrl = imageService.uploadToStorage("team", fileName, file);
                    }
                } else {
                    // image 필드가 null 일 경우 비교 없이 바로 업로드
                    System.out.println("이미지 새 업로드!");
                    imgUrl = imageService.uploadToStorage("team", fileName, file);
                }

            }

            //업데이트
            team.updateTeam(req.getTitle(), req.getStartDate(), req.getEndDate(), imgUrl);
            return ResponseEntity.ok(new JsonResponse(200, "Success", teamIdx));

        } else {
            //요청 사용자와 팀짱이 동일하지 않음
            System.out.println("not same");
            return ResponseEntity.ok(new JsonResponse(2000, "권한이 없습니다. 팀장만 가능합니다.", null));
        }
    }
}
