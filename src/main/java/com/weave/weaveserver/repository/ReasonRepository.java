package com.weave.weaveserver.repository;

import com.weave.weaveserver.domain.Plan;
import com.weave.weaveserver.domain.Reason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReasonRepository extends JpaRepository<Reason, Long> {

}
