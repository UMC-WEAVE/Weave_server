package com.weave.weaveserver.repository;

import com.weave.weaveserver.domain.Team;
import com.weave.weaveserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TeamRepository  extends JpaRepository<Team,Integer> {

}
