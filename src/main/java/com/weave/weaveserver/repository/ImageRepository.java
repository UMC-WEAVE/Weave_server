package com.weave.weaveserver.repository;

import com.weave.weaveserver.domain.Image;
import com.weave.weaveserver.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
