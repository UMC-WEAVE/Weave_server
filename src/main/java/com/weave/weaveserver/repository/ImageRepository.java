package com.weave.weaveserver.repository;

import com.weave.weaveserver.domain.Archive;
import com.weave.weaveserver.domain.Image;
import com.weave.weaveserver.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

//    @Query(value = "SELECT i from Image i where i.archive.archiveIdx = :archiveIdx")
//    List<Archive> findByArchiveIdx(@Param("archiveIdx") Long archiveIdx);

    void deleteByArchive(Archive archive);
}
