package com.weave.weaveserver.repository;

import com.weave.weaveserver.domain.Archive;
import com.weave.weaveserver.dto.ArchiveResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface ArchiveRepository extends JpaRepository<Archive, Long> {

    Archive findByArchiveIdx(Long archiveIdx);

    @Query(value = "SELECT a from Archive a where a.team.teamIdx = :teamIdx")
    List<Archive> findByTeamIdx(@Param("teamIdx") Long teamIdx);

    void deleteByArchiveIdx(Long archiveIdx);
}
