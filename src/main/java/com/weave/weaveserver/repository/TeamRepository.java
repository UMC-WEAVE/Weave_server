package com.weave.weaveserver.repository;

import com.weave.weaveserver.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TeamRepository  extends JpaRepository<Team, Long> {

}
