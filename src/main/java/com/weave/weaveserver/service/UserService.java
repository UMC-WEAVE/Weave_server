package com.weave.weaveserver.service;

import com.weave.weaveserver.config.exception.BadRequestException;
import com.weave.weaveserver.domain.Archive;
import com.weave.weaveserver.domain.Plan;
import com.weave.weaveserver.domain.Team;
import com.weave.weaveserver.domain.User;
import com.weave.weaveserver.dto.PlanResponse;
import com.weave.weaveserver.dto.UserRequest;
import com.weave.weaveserver.dto.UserResponse;
import com.weave.weaveserver.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BelongRepository belongRepository;
//    private final PlanService planService;

    @Transactional
    public void joinUser(UserRequest.join joinUser) {
        User user = User.joinUser(joinUser);
        System.out.println(""+user.getImage()+user.getName());
        userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        try{
            return userRepository.findUserByEmail(email);
        }catch (NullPointerException e){
            throw new BadRequestException("등록되지 않은 유저입니다.");
        }
    }


    @Transactional
    public UserResponse.myPage loadMyPage(String email) {
        User user = userRepository.findUserByEmail(email);
        Long countTeam = belongRepository.countTeamByUser(user.getUserIdx());
        //countTeamIdx
        return UserResponse.myPage.builder()
                .email(user.getEmail()).name(user.getName()).image(user.getImage()).countTeam(countTeam)
                .build();
    }

    public void loginUser(User user) {
        userRepository.save(user);
    }
}
