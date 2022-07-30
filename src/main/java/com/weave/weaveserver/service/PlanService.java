package com.weave.weaveserver.service;

import com.weave.weaveserver.domain.Archive;
import com.weave.weaveserver.domain.Plan;
import com.weave.weaveserver.domain.Team;
import com.weave.weaveserver.domain.User;
import com.weave.weaveserver.dto.PlanRequest;
import com.weave.weaveserver.dto.PlanResponse;
import com.weave.weaveserver.repository.ArchiveRepository;
import com.weave.weaveserver.repository.PlanRepository;
import com.weave.weaveserver.repository.TeamRepository;
import com.weave.weaveserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service

@RequiredArgsConstructor
public class PlanService {
    public final UserRepository userRepository;
    public final TeamRepository teamRepository;
    public final PlanRepository planRepository;
    public final ArchiveRepository archiveRepository;

    @Transactional
    public Long addPlan(PlanRequest.createReq req){
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
                .isModified(false)

                .build();

        if (req.getIsArchive() == 1){ //archive 필요
            System.out.println("isArchive!!");
            //Archive table에 isPinned = true해주기
            //Archive가져오려면 archiveIdx도 같이 줘야함
            Archive archive = archiveRepository.getReferenceById(req.getArchiveIdx());
            archive.activatePin();
        }

        return planRepository.save(plan).getPlanIdx();

    }

    @Transactional
    public PlanResponse.planDetailRes getPlanDetail(Long planIdx){
        Plan plan = planRepository.getReferenceById(planIdx);

        PlanResponse.planDetailRes dto = new PlanResponse.planDetailRes(
                plan.getPlanIdx(),
                plan.getTeam().getTeamIdx(),
                plan.getDate(),
                plan.dayOfDate(plan.getDate()),
                plan.getTitle(),
                plan.getStartTime(),
                plan.getEndTime(),
                plan.getLocation(),
                plan.getCost(),
                plan.getUser().getUserIdx(),
                plan.isModified()
        );

        return dto;
    }

    @Transactional
    public PlanResponse.planListRes getPlanList(Long teamIdx){
        PlanResponse.planListRes planListRes = new PlanResponse.planListRes();

        List<Plan> planList = planRepository.findAllByTeamIdx(teamIdx);

        List<PlanResponse.planDetailRes> detailListDto = planList.stream().map(plan -> new PlanResponse.planDetailRes(
                plan.getPlanIdx(),
                plan.getTeam().getTeamIdx(),
                plan.getDate(),
                plan.dayOfDate(plan.getDate()),
                plan.getTitle(),
                plan.getStartTime(),
                plan.getEndTime(),
                plan.getLocation(),
                plan.getCost(),
                plan.getUser().getUserIdx(),
                plan.isModified()
                )
        ).collect(Collectors.toList());

        planListRes.setPlanDto(detailListDto);


        return planListRes;
    }

    @Transactional
    public Long deletePlan(Long planIdx){
        planRepository.deleteById(planIdx); return planIdx;
    }

    @Transactional
    public void updatePlan(Long planIdx, PlanRequest.createReq req){
        Plan plan = planRepository.getReferenceById(planIdx);
        User user = userRepository.getReferenceById(req.getUserIdx());
        plan.updatePlan(user,
                req.getTitle(),
                //req.getDate(),
                LocalDate.now(),
//                req.getStartTime(),
                LocalDateTime.now(),
//                req.getEndTime(),
                LocalDateTime.now(),
                req.getLocation(),
                req.getLatitude(),
                req.getLongitude(),
                req.getCost());
//        planRepository.save(plan);

    }

}
