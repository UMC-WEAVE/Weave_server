
package com.weave.weaveserver.service;

import com.weave.weaveserver.config.exception.BadRequestException;
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
    public void createTeam(TeamRequest.createReq req, HttpServletRequest httpServletRequest) {
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
        teamRepository.save(team);

        Belong belong = Belong.builder()
                .user(leader)
                .team(team)
                .build();
        belongRepository.save(belong);
    }

    @Transactional
    public int addMember(Long teamIdx, TeamRequest.addMemberReq req, HttpServletRequest httpServletRequest) {

        // 해당 팀 IDX 가 존재하지 않는 경우에 대한 예외 처리
        Team team = teamRepository.findById(teamIdx)
                .orElseThrow(() -> new NotFoundException("해당 팀이 존재하지 않습니다."));

        User leader = team.getLeader();

        String userEmail = tokenService.getUserEmail(httpServletRequest);
        User creator = userRepository.findUserByEmail(userEmail);

        if(leader.equals(creator)){
            //생성하려는 사용자와 팀짱이 동일
            System.out.println("same");


        } else {
            //생성하려는 사용자와 팀짱이 동일하지 않음
            System.out.println("not same");
            return 3000;
        }


        // find user (존재하는 사용자인지 확인)
        User invitedUser = userRepository.findUserByEmail(req.getEmail());
        if(invitedUser != null){
            // 이미 팀에 존재하는 유저인지 확인
            //System.out.println("이미 초대된 팀원입니다");

            System.out.println("초대 팀원 존재 : "+ invitedUser.getEmail());

            Long userIdx = invitedUser.getUserIdx();
            User user = userRepository.getReferenceById(userIdx);

            Belong belong = Belong.builder()
                    .user(user)
                    .team(team)
                    .build();

            belongRepository.save(belong);

            //team에 유저가 1명 이상 -> isEmpty = false;
            team.updateEmpty();

            return 1;

        } else {
            System.out.println("초대 팀원이 존재하지 않음");
            return 0;
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
                "user Image url"
        )).collect(Collectors.toList());

        return memberList;
    }

    @Transactional
    public List<TeamResponse.teamResponse> getMyTeams(HttpServletRequest httpServletRequest){

        String userEmail = tokenService.getUserEmail(httpServletRequest);
        User user = userRepository.findUserByEmail(userEmail);
        //System.out.println(userEmail);
        //System.out.println(user.getUserIdx());
        List<Team> teams = belongRepository.findAllByUserIdx(user.getUserIdx());

        List<TeamResponse.teamResponse> teamList = teams.stream().map(team -> new TeamResponse.teamResponse(
                team.getTeamIdx(),
                team.getTitle(),
                team.getStartDate(),
                team.getEndDate(),
                "user Image url"
        )).collect(Collectors.toList());

        return teamList;
    }

    @Transactional
    public void deleteTeam(Long teamIdx, HttpServletRequest httpServletRequest){
        //팀짱인지 확인 과정 필요

        // 해당 팀 IDX 가 존재하지 않는 경우에 대한 예외 처리
        Team team = teamRepository.findById(teamIdx)
                .orElseThrow(() -> new NotFoundException("해당 팀이 존재하지 않습니다."));

        User leader = team.getLeader();

        String userEmail = tokenService.getUserEmail(httpServletRequest);
        User requester = userRepository.findUserByEmail(userEmail);

        if(leader.equals(requester)){
            //요청 사용자와 팀짱이 동일
            System.out.println("same");
            teamRepository.deleteByTeamIdx(teamIdx);

        } else {
            //요청 사용자와 팀짱이 동일하지 않음
            System.out.println("not same");
        }

    }

    @Transactional
    public void deleteMember(Long teamIdx, TeamRequest.deleteMemberReq req, HttpServletRequest httpServletRequest){

        // 해당 팀 IDX 가 존재하지 않는 경우에 대한 예외 처리
        Team team = teamRepository.findById(teamIdx)
                .orElseThrow(() -> new NotFoundException("해당 팀이 존재하지 않습니다."));

        User leader = team.getLeader();

        String userEmail = tokenService.getUserEmail(httpServletRequest);
        User requester = userRepository.findUserByEmail(userEmail);

        if(leader.equals(requester)){
            //요청 사용자와 팀짱이 동일
            System.out.println("same");



            // 삭제하려는 팀원이 팀 소속인지 확인



            // 소속이라면
            Belong deleteUser = belongRepository.findUserByIndex(teamIdx, req.getUserIdx());
            belongRepository.deleteById(deleteUser.getBelongIdx());

        } else {
            //요청 사용자와 팀짱이 동일하지 않음
            System.out.println("not same");
        }


    }

    @Transactional
    public void updateTeam(Long teamIdx, TeamRequest.updateTeamReq req, HttpServletRequest httpServletRequest){
        //팀짱인지 확인 필요

        // 해당 팀 IDX 가 존재하지 않는 경우에 대한 예외 처리
        Team team = teamRepository.findById(teamIdx)
                .orElseThrow(() -> new NotFoundException("해당 팀이 존재하지 않습니다."));

        User leader = team.getLeader();

        String userEmail = tokenService.getUserEmail(httpServletRequest);
        User requester = userRepository.findUserByEmail(userEmail);

        if(leader.equals(requester)){
            //요청 사용자와 팀짱이 동일
            System.out.println("same");

            //업데이트
            team.updateTeam(req.getTitle(), req.getStartDate(), req.getEndDate(), req.getImgUrl());

        } else {
            //요청 사용자와 팀짱이 동일하지 않음
            System.out.println("not same");
        }
    }
}
