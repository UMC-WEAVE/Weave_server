package com.weave.weaveserver.service;

import com.weave.weaveserver.domain.Plan;
import com.weave.weaveserver.domain.User;
import com.weave.weaveserver.dto.PlanResponse;
import com.weave.weaveserver.dto.UserRequest;
import com.weave.weaveserver.dto.UserResponse;
import com.weave.weaveserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void joinUser(UserRequest.join joinUser) {
        User user = User.builder().email(joinUser.getEmail()).name(joinUser.getName()).loginId(joinUser.getLoginId()).build();
        userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public void deleteUser(String email) {
        User user = getUserByEmail(email);
        userRepository.delete(user);
    }
}
