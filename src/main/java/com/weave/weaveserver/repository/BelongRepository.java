package com.weave.weaveserver.repository;

import com.weave.weaveserver.domain.Belong;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BelongRepository extends JpaRepository<Belong,Integer> {
}
