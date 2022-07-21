package com.weave.weaveserver.repository;

import com.weave.weaveserver.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
