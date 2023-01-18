package com.weave.weaveserver.repository;

import com.weave.weaveserver.domain.Plan;
import com.weave.weaveserver.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public interface PlanRepository extends JpaRepository<Plan, Long> {
    @Query(value = "SELECT p.planIdx "
            + "FROM Plan p "
            + "WHERE p.team.teamIdx = ?1 ")
    List<Long> findAllByTeamIdx(Long teamIdx);

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

    @Query(value = "SELECT p.planIdx FROM Plan p WHERE p.team.teamIdx = ?1")
    Optional<List<Long>> findALLByTeamIdx(Long teamIdx);

    Plan findByPlanIdx(Long planIdx);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Plan p WHERE p.user.userIdx=?1")
    void deleteAllByUserIdx(Long userIdx);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Plan p WHERE p.team.teamIdx=?1")
    void deleteAllByTeamIdx(Long teamIdx);

}

