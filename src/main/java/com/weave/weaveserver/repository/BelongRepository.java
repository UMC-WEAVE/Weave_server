package com.weave.weaveserver.repository;

import com.weave.weaveserver.domain.Belong;
import com.weave.weaveserver.domain.Team;
import com.weave.weaveserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BelongRepository extends JpaRepository<Belong, Long> {
    @Query(value = "SELECT b.user "
            + "FROM Belong b "
            + "WHERE b.team.teamIdx = ?1  ")
    List<User> findAllByTeamIdx(Long teamIdx);

    @Query(value = "SELECT b.team "
            + "FROM Belong b "
            + "WHERE b.user.userIdx = ?1  ")
    List<Team> findAllByUserIdx(Long userIdx);


    @Query(value = "SELECT b "
            + "FROM Belong b "
            + "WHERE b.team.teamIdx =?1 and b.user.userIdx =?2")
    Belong findUserByIndex(Long teamIdx, Long userIdx);

    //void deleteById(Long belongIdx);


    //jpa에서는 count query를 Long type으로 return
    @Query(value = "SELECT COUNT(b.team) FROM Belong b WHERE b.user.userIdx=?1")
    Long countTeamByUser(Long userIdx);
    
    @Query(value = "select b " +
            "from Belong b " +
            "where b.team.teamIdx = :teamIdx " +
            "and b.user.email = :userEmail")
    Belong findByTeamIdxAndUser(@Param("teamIdx") Long teamIdx, @Param("userEmail") String userEmail);


    @Query(value = "SELECT COUNT(b.user) FROM Belong b WHERE b.team.teamIdx=?1")
    Long countMemberByTeam(Long teamIdx);


    void deleteByUser(User user);
}
