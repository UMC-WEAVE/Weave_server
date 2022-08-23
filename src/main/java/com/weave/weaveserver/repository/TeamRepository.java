package com.weave.weaveserver.repository;

import com.weave.weaveserver.domain.Plan;
import com.weave.weaveserver.domain.Team;
import com.weave.weaveserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface TeamRepository  extends JpaRepository<Team, Long> {

    Team findByTeamIdx(Long teamIdx);

    void deleteByTeamIdx(Long teamIdx);

    @Query(value = "SELECT t FROM Team t WHERE t.leader.userIdx = ?1")
    Optional<List<Team>> findALLByLeaderIdx(Long userIdx);
}
