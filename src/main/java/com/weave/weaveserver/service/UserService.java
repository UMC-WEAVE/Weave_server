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

    private final TeamRepository teamRepository;
    private final PlanRepository planRepository;
    private final ArchiveRepository archiveRepository;

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


    @Transactional
    public void deleteUser(String email) {
        //TODO : test 12번 유저
        try {
            User user = userRepository.findUserByEmail(email);
        }catch (NullPointerException e){
            throw new BadRequestException("이미 삭제된 유저");
        }
        User user = userRepository.findUserByEmail(email);
        if(user==null){
            throw new BadRequestException("존재하지 않는 token입니다.");
        }
        try{
            planService.deletePlanByUserIdx(user.getUserIdx());
//            deleteTeamByLeaderIdx(user.getUserIdx());
//            //deletePlanByUserIdx(user.getUserIdx());
//            deleteArchiveByUserIdx(user.getUserIdx());

        }catch (BadRequestException e){
            System.out.println("등록되지 않은 팀이래요~~");
        }
        userRepository.delete(user);
        System.out.println(user.getUserIdx()+"번째 유저 삭제 완료");
    }

    //team 삭제시 plan, archive 전부 삭제 => teamIdx
    @Transactional
    public void deleteTeamByLeaderIdx(Long userIdx){
        List<Team> teamList = teamRepository.findALLByLeaderIdx(userIdx).orElseThrow(()->new BadRequestException("team이 등록되어있지 않은 user"));
        for(Team team : teamList){
            System.out.println("deleteTeam : "+team.getTeamIdx());
            deleteArchiveByTeamIdx(team.getTeamIdx());
            deletePlanByTeamIdx(team.getTeamIdx());
            teamRepository.delete(team);
        }
        System.out.println("팀삭제 끝!");
    }

    //user 탈퇴시 -> plan 삭제
    //team 삭제시 -> plan 삭제
    @Transactional
    public void deletePlanByUserIdx(Long userIdx){
        planRepository.deleteAllByUserIdx(userIdx);
//        List<Plan> planList = planRepository.findALLByUserIdx(userIdx).orElseThrow(()->new BadRequestException("plan이 등록되어있지 않은 user"));
//        for(Plan plan : planList){
//            System.out.println("deletePlan : "+plan.getPlanIdx());
//            planRepository.delete(plan);
//        }
//        System.out.println("플랜 삭제 끝!");
    }

    @Transactional
    public void deletePlanByTeamIdx(Long teamIdx){
        List<Plan> planList = planRepository.findALLByTeamIdx(teamIdx).orElseThrow(()->new BadRequestException("plan이 등록되어있지 않은 user"));
        for(Plan plan : planList){
            System.out.println("deletePlan : "+plan.getPlanIdx());
            planRepository.delete(plan);
        }
        System.out.println("플랜 삭제 끝!");
    }

    //user 탈퇴시 -> archive (user = null)
    //team 삭제시 -> archive 삭제
    @Transactional
    public void deleteArchiveByUserIdx(Long userIdx){
        List<Archive> archiveList = archiveRepository.findAllByUserIdx(userIdx).orElseThrow(()->new BadRequestException("archive가 등록되어있지 않은 user"));
        for(Archive archive : archiveList){
            System.out.println("deleteArchive : "+archive.getArchiveIdx());
            archiveRepository.delete(archive);
//            System.out.println("updateArchive => user null : "+archive.getArchiveIdx());
//            archive.setUser(null);
//            archiveRepository.save(archive);
        }
        System.out.println("아카이브 삭제 끝!");
    }

    @Transactional
    public void deleteArchiveByTeamIdx(Long teamIdx){
        List<Archive> archiveList = archiveRepository.findAllByTeamIdx(teamIdx).orElseThrow(()->new BadRequestException("archive가 등록되어있지 않은 user"));
        for(Archive archive : archiveList){
            System.out.println("deleteArchive : "+archive.getArchiveIdx());
            archiveRepository.delete(archive);
        }
        System.out.println("아카이브 삭제 끝!");
    }


}
