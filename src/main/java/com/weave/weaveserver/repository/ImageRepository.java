package com.weave.weaveserver.repository;

import com.weave.weaveserver.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
