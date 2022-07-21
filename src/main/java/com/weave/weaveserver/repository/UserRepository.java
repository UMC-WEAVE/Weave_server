package com.weave.weaveserver.repository;


import com.weave.weaveserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    User findByEmail(String email);

    User findUserByEmail(String email);
}
