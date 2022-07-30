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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
            return 1;

        } else {
            System.out.println("초대 팀원이 존재하지 않음");
            return 0;
        }
    }

    //팀원 조회

}
