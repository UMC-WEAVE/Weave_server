package com.weave.weaveserver.service;

import com.weave.weaveserver.domain.Plan;
import com.weave.weaveserver.domain.Team;
import com.weave.weaveserver.domain.User;
import com.weave.weaveserver.dto.PlanRequest;
import com.weave.weaveserver.dto.PlanResponse;
import com.weave.weaveserver.dto.UserResponse;
import com.weave.weaveserver.repository.PlanRepository;
import com.weave.weaveserver.repository.TeamRepository;
import com.weave.weaveserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service

@RequiredArgsConstructor
public class PlanService {
    public final UserRepository userRepository;
    public final TeamRepository teamRepository;
    public final PlanRepository planRepository;

    public void addPlan(PlanRequest.createReq req){
        User user = userRepository.getReferenceById(req.getUserIdx());
        Team team = teamRepository.getReferenceById(req.getTeamIdx());
        Plan plan = Plan.builder()
                .team(team)
                .user(user)
                .title(req.getTitle())

                .date(req.getDate())
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())

                .location(req.getLocation())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .cost(req.getCost())
                .build();

        planRepository.save(plan);
    }

    public PlanResponse.planRes getPlan(Long planIdx){
        Plan plan = planRepository.getReferenceById(planIdx);
        PlanResponse.planRes dto = new PlanResponse.planRes(
                plan.getPlanIdx(),
                plan.getTeam().getTeamIdx(),
                plan.getDate(),
                plan.getTitle(),
                plan.getStartTime(),
                plan.getEndTime(),
                plan.getLocation(),
                plan.getCost(),
                plan.getUser().getUserIdx()
        );

        return dto;
    }

    public List<PlanResponse.planRes> getPlanList(int teamIdx){
        List<Plan> planList = planRepository.findAllByTeamIdx(teamIdx);

        List<PlanResponse.planRes> list = planList.stream().map(plan -> new PlanResponse.planRes(
                plan.getPlanIdx(),
                plan.getTeam().getTeamIdx(),
                plan.getDate(),
                plan.getTitle(),
                plan.getStartTime(),
                plan.getEndTime(),
                plan.getLocation(),
                plan.getCost(),
                plan.getUser().getUserIdx())
        ).collect(Collectors.toList());

        return list;
    }

    public void deletePlan(Long planIdx){
        planRepository.deleteById(planIdx);
    }

    public void updatePlan(Long planIdx, PlanRequest.createReq req){
        Plan plan = planRepository.getReferenceById(planIdx);
        User user = userRepository.getReferenceById(req.getUserIdx());
        plan.updatePlan(user, req.getTitle(), req.getStartTime(), req.getEndTime(), req.getLocation(), req.getCost());
        planRepository.save(plan);

    }

}
