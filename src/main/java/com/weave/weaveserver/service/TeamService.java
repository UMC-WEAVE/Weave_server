package com.weave.weaveserver.service;

import com.weave.weaveserver.config.exception.NotFoundException;
import com.weave.weaveserver.config.jwt.TokenService;
import com.weave.weaveserver.domain.Belong;
import com.weave.weaveserver.domain.Team;
import com.weave.weaveserver.domain.User;
import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.dto.TeamRequest;
import com.weave.weaveserver.dto.TeamResponse;
import com.weave.weaveserver.repository.BelongRepository;
import com.weave.weaveserver.repository.TeamRepository;
import com.weave.weaveserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    @Autowired
    private TokenService tokenService;

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final BelongRepository belongRepository;


    @Transactional
    public ResponseEntity<JsonResponse> createTeam(TeamRequest.createReq req, HttpServletRequest httpServletRequest) {

        String userEmail = tokenService.getUserEmail(httpServletRequest);
        User leader = userRepository.findUserByEmail(userEmail);

        System.out.println(leader.getUserIdx());

        Team team = Team.builder()
                .leader(leader)
                .title(req.getTitle())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .imgUrl(req.getImgUrl())
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
                    System.out.println("팀짱이고 사용자도 존재하고, 팀에도 없어요~!!");

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
            User user = userRepository.findUserByEmail(req.getEmail());

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
    public ResponseEntity<JsonResponse> updateTeam(Long teamIdx, TeamRequest.updateTeamReq req, HttpServletRequest httpServletRequest){

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
            //업데이트
            team.updateTeam(req.getTitle(), req.getStartDate(), req.getEndDate(), req.getImgUrl());
            return ResponseEntity.ok(new JsonResponse(200, "Success", teamIdx));

        } else {
            //요청 사용자와 팀짱이 동일하지 않음
            System.out.println("not same");
            return ResponseEntity.ok(new JsonResponse(2000, "권한이 없습니다. 팀장만 가능합니다.", null));
        }
    }
}
