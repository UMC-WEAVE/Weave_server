package com.weave.weaveserver.repository;

import com.weave.weaveserver.domain.Archive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ArchiveRepository extends JpaRepository<Archive, Long> {
//    @Query(value = "SELECT ")
    //TODO : 작성

}
