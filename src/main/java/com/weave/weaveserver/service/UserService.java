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

    private final PlanService planService;

    @Transactional
    public void joinUser(UserRequest.join joinUser) {
        User user = User.joinUser(joinUser);
        System.out.println(""+user.getImage()+user.getName());
        userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
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


//    @Transactional
//    public void deleteUser(String email) {
//        //TODO : test 12번 유저
//        try {
//            User user = userRepository.findUserByEmail(email);
//        }catch (NullPointerException e){
//            throw new BadRequestException("이미 삭제된 유저");
//        }
//        User user = userRepository.findUserByEmail(email);
//        if(user==null){
//            throw new BadRequestException("존재하지 않는 token입니다.");
//        }
//        try{
//            deleteTeamByLeaderIdx(user.getUserIdx());
//            deletePlanByUserIdx(user.getUserIdx());
//            deleteArchiveByUserIdx(user.getUserIdx());
//
//        }catch (BadRequestException e){
//            System.out.println("등록되지 않은 팀이래요~~");
//        }
//        userRepository.delete(user);
//        System.out.println(user.getUserIdx()+"번째 유저 삭제 완료");
//    }


}
