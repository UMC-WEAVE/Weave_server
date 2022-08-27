package com.weave.weaveserver.repository;

import com.weave.weaveserver.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface PlanRepository extends JpaRepository<Plan, Long> {
    @Query(value = "SELECT p "
            + "FROM Plan p "
            + "WHERE p.team.teamIdx = ?1 ")
    List<Plan> findAllByTeamIdx(Long teamIdx);

    @Query(value = "SELECT p "
            + "FROM Plan p "
            + "WHERE p.team.teamIdx = ?1 "
            + "ORDER BY p.date, p.startTime")
    List<Plan> findAllByTeamIdxOrderByDateAndStartTime(Long teamIdx);

    @Query(value = "SELECT p "
            + "FROM Plan p "
            + "WHERE p.team.teamIdx = ?1 ")
    List<Plan> getPointsByTeamIdx(Long teamIdx);


    @Query(value = "SELECT p FROM Plan p WHERE p.user.userIdx = ?1")
    Optional<List<Plan>> findALLByUserIdx(Long userIdx);

    @Query(value = "SELECT p FROM Plan p WHERE p.team.teamIdx = ?1")
    Optional<List<Plan>> findALLByTeamIdx(Long teamIdx);

    Plan findByPlanIdx(Long planIdx);
}
