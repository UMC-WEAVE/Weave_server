package com.weave.weaveserver.service;

import com.weave.weaveserver.domain.Plan;
import com.weave.weaveserver.domain.User;
import com.weave.weaveserver.dto.PlanRequest;
import com.weave.weaveserver.dto.PlanResponse;
import com.weave.weaveserver.dto.UserResponse;
import com.weave.weaveserver.repository.PlanRepository;
import com.weave.weaveserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service

@RequiredArgsConstructor
public class PlanService {
    public final UserRepository userRepository;
    public final PlanRepository planRepository;

    public void addPlan(PlanRequest.createReq req){
        User user = userRepository.getReferenceById(req.getUserIdx());
        Plan plan = Plan.builder()
                .teamIdx(1)
                .user(user)
                .title(req.getTitle())
                .date(req.getDate())

                .startTime(req.getStart_time())
                .endTime(req.getEnd_time())

                .location(req.getLocation())
                .latitude((float)1)
                .longitude((float)1)
                .cost(5000)
                .build();
        planRepository.save(plan);
    }

    public PlanResponse.planRes getPlan(int planIdx){
        Plan plan = planRepository.getReferenceById(planIdx);
        PlanResponse.planRes dto = new PlanResponse.planRes(
                plan.getPlanIdx(),
                plan.getTeamIdx(),
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
                1,
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

    public void deletePlan(int planIdx){
        planRepository.deleteById(planIdx);
    }

    public void updatePlan(int planIdx, PlanRequest.createReq req){
        Plan plan = planRepository.getReferenceById(planIdx);
        User user = userRepository.getReferenceById(req.getUserIdx());
        plan.updatePlan(user, req.getTitle(), req.getStart_time(), req.getEnd_time(), req.getLocation(), req.getCost());
        planRepository.save(plan);

    }

}
