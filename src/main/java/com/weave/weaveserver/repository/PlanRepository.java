package com.weave.weaveserver.repository;

import com.weave.weaveserver.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Integer> {
    @Query(value = "SELECT p "
            + "FROM Plan p "
            + "WHERE p.teamIdx = ?1 ")
    List<Plan> findAllByTeamIdx(int teamIdx);
}
