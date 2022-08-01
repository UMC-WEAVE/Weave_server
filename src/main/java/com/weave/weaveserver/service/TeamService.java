
package com.weave.weaveserver.service;

import com.weave.weaveserver.domain.Belong;
import com.weave.weaveserver.domain.Team;
import com.weave.weaveserver.domain.User;
import com.weave.weaveserver.dto.TeamRequest;
import com.weave.weaveserver.dto.TeamResponse;
import com.weave.weaveserver.repository.BelongRepository;
import com.weave.weaveserver.repository.TeamRepository;
import com.weave.weaveserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final BelongRepository belongRepository;

    @Transactional
    public void createTeam(Long leaderIdx, TeamRequest.createReq req) {
        User leader = userRepository.getReferenceById(leaderIdx);

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
    public int addMember(Long teamIdx, TeamRequest.addMemberReq req) {

        // team creator == request user (생성하려는 사용자와 팀짱이 동일인인지 확인)
        Team team = teamRepository.getReferenceById(teamIdx);
//        User creator = team.getLeader();
//
//        if(!(creator.getUserIdx().equals(req.getLeaderIdx()))){
//            System.out.println("생성자와 팀짱이 일치하지 않음");
//            return 0;
//        }

        // find user (존재하는 사용자인지 확인)
        User invitedUser = userRepository.findUserByEmail(req.getEmail());
        if(invitedUser != null){
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
        List<User> userList = belongRepository.findAllByTeamIdx(teamIdx);

        List<TeamResponse.getMemberList> memberList = userList.stream().map(user -> new TeamResponse.getMemberList(
                user.getUserIdx(),
                user.getName(),
                "user Image url"
        )).collect(Collectors.toList());

        return memberList;
    }

    @Transactional
    public List<TeamResponse.teamResponse> getMyTeams(Long userIdx){
        List<Team> teams = belongRepository.findAllByUserIdx(userIdx);

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
    public void deleteTeam(Long teamIdx, TeamRequest.deleteTeamReq req){
        //팀짱인지 확인 과정 필요

        teamRepository.deleteByTeamIdx(teamIdx);
    }

    @Transactional
    public void deleteMember(Long teamIdx, TeamRequest.deleteMemberReq req){
        //팀짱인지 확인 과정 필요

        Belong deleteUser = belongRepository.findUserByIndex(teamIdx, req.getUserIdx());
        belongRepository.deleteById(deleteUser.getBelongIdx());
    }

    @Transactional
    public void updateTeam(Long teamIdx, TeamRequest.updateTeamReq req){
        //팀짱인지 확인 필요

        Team team = teamRepository.getReferenceById(teamIdx);
        team.updateTeam(req.getTitle(), req.getStartDate(), req.getEndDate(), req.getImgUrl());
    }
}
