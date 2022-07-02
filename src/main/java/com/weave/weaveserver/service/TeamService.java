package com.weave.weaveserver.service;

import com.weave.weaveserver.domain.Belong;
import com.weave.weaveserver.domain.Team;
import com.weave.weaveserver.domain.User;
import com.weave.weaveserver.dto.TeamRequest;
import com.weave.weaveserver.repository.BelongRepository;
import com.weave.weaveserver.repository.TeamRepository;
import com.weave.weaveserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final BelongRepository belongRepository;

    public void createTeam(TeamRequest.createReq req) {
        User user = userRepository.getReferenceById(req.getUserIdx());
        Team team = Team.builder()
                .user(user)
                .title(req.getTitle())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .location(req.getLocation())
                .imgUrl(req.getImgUrl())
                .build();

        teamRepository.save(team);

        Belong belong = Belong.builder()
                .user(user)
                .team(team)
                .build();

        belongRepository.save(belong);
    }

    public void addMember(int teamIdx, TeamRequest.addMemberReq req) {
        //userfind equal ...
        //User email = userRepository.findByEmail(req.getEmail());

        Team team = teamRepository.getReferenceById(teamIdx);
        String email = req.getEmail();
        int userIdx = 2;
        User user = userRepository.getReferenceById(userIdx);
        Belong belong = Belong.builder()
                .user(user)
                .team(team)
                .build();

        belongRepository.save(belong);

    }

}
