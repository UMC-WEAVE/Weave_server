package com.weave.weaveserver.repository;

import com.weave.weaveserver.domain.Archive;
import com.weave.weaveserver.domain.Team;
import com.weave.weaveserver.dto.ArchiveResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface ArchiveRepository extends JpaRepository<Archive, Long> {

    //이걸 findById로 대체해보려고 했는데 이미 이 함수를 사용중인 다른 파일도 많고 findById는 null이 반환될 때의 처리도 해주어야 해서 뭔가 깔끔하지 않아진다.고민..
    Archive findByArchiveIdx(Long archiveIdx);

//    @Query(value = "SELECT a from Archive a where a.team.teamIdx = :teamIdx") //이 경우 @Param("teamIdx") Long teamIdx 이런식으로 명시해줘야 함
    List<Archive> findByTeam(Team team);

    void deleteByArchiveIdx(Long archiveIdx);

    @Query(value = "SELECT a FROM Archive a WHERE a.user.userIdx = ?1")
    Optional<List<Archive>> findAllByUserIdx(Long userIdx);

    @Query(value = "SELECT a FROM Archive a WHERE a.team.teamIdx = ?1")
    Optional<List<Archive>> findAllByTeamIdx(Long teamIdx);
}
