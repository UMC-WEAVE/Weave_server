package com.weave.weaveserver.service;

import com.weave.weaveserver.config.exception.BadRequestException;
import com.weave.weaveserver.domain.User;
import com.weave.weaveserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserProvider {
    private final UserRepository userRepository;

    public User getUserByEmail(String email) {
        try{
            return userRepository.findUserByEmail(email);
        }catch (NullPointerException e){
            throw new BadRequestException("등록되지 않은 유저입니다.");
        }
    }

    //getUserByEmail를 이 메서드로 바꿔주시면 됩니다~~
    public User getUserByUuid(String uuid){
        try{
            return userRepository.findUserByUuid(uuid);
        }catch (NullPointerException e){
            throw new BadRequestException("등록되지 않은 유저입니다.");
        }
    }


}
