package com.weave.weaveserver.repository;

import com.weave.weaveserver.domain.Belong;
import com.weave.weaveserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BelongRepository extends JpaRepository<Belong, Long> {
    @Query(value = "SELECT b.user "
            + "FROM Belong b "
            + "WHERE b.team.teamIdx = ?1  ")
    List<User> findAllByTeamIdx(Long teamIdx);
}
