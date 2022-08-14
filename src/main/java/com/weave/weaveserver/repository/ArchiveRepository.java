package com.weave.weaveserver.repository;

import com.weave.weaveserver.domain.Archive;
import com.weave.weaveserver.domain.Team;
import com.weave.weaveserver.dto.ArchiveResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface ArchiveRepository extends JpaRepository<Archive, Long> {

    Archive findByArchiveIdx(Long archiveIdx);

//    @Query(value = "SELECT a from Archive a where a.team.teamIdx = :teamIdx") //이 경우 @Param("teamIdx") Long teamIdx 이런식으로 명시해줘야 함
    List<Archive> findByTeam(Team team);

    void deleteByArchiveIdx(Long archiveIdx);
}
