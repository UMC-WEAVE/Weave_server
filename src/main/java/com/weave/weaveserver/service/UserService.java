package com.weave.weaveserver.service;

import com.weave.weaveserver.config.exception.BadRequestException;
import com.weave.weaveserver.domain.Plan;
import com.weave.weaveserver.domain.User;
import com.weave.weaveserver.dto.PlanResponse;
import com.weave.weaveserver.dto.UserRequest;
import com.weave.weaveserver.dto.UserResponse;
import com.weave.weaveserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void joinUser(UserRequest.join joinUser) {
        User user = User.joinUser(joinUser);
        System.out.println(""+user.getImage()+user.getName());
        userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(()->new BadRequestException("유저의 정보를 찾을 수 없음"));
    }

    @Transactional
    public void deleteUser(String email) {
        User user = userRepository.findUserByEmail(email).orElseThrow(()-> new BadRequestException("유저의 정보를 찾을 수 없음"));
        userRepository.delete(user);
    }

    @Transactional
    public UserResponse.myPage loadMyPage(String email) {
        User user = userRepository.findUserByEmail(email).orElseThrow(()-> new BadRequestException("유저의 정보를 찾을 수 없음"));
        //countTeamIdx
        return UserResponse.myPage.builder()
                .email(user.getEmail()).name(user.getName()).image(user.getImage()).countTeam(10)
                .build();
    }
}
