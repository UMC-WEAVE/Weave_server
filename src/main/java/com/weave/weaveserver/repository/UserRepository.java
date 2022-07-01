package com.weave.weaveserver.repository;


import com.weave.weaveserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {

}
