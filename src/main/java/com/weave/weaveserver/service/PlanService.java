package com.weave.weaveserver.service;

import com.weave.weaveserver.config.exception.BadRequestException;
import com.weave.weaveserver.config.exception.EntityNotFoundException;
import com.weave.weaveserver.config.exception.GlobalException;
import com.weave.weaveserver.config.exception.NotFoundException;
import com.weave.weaveserver.config.jwt.TokenService;
import com.weave.weaveserver.domain.Archive;
import com.weave.weaveserver.domain.Plan;
import com.weave.weaveserver.domain.Team;
import com.weave.weaveserver.domain.User;
import com.weave.weaveserver.dto.MapResponse;
import com.weave.weaveserver.dto.PlanRequest;
import com.weave.weaveserver.dto.PlanResponse;
import com.weave.weaveserver.dto.TeamResponse;
import com.weave.weaveserver.repository.ArchiveRepository;
import com.weave.weaveserver.repository.PlanRepository;
import com.weave.weaveserver.repository.TeamRepository;
import com.weave.weaveserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.parser.Entity;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service

@RequiredArgsConstructor
public class PlanService {
    public final TeamService teamService;
    public final TokenService tokenService;

    public final UserRepository userRepository;
    public final TeamRepository teamRepository;
    public final PlanRepository planRepository;
    public final ArchiveRepository archiveRepository;

    @Transactional
    public Long addPlan(PlanRequest.createReq req, HttpServletRequest httpServletRequest){
        if(httpServletRequest == null){
            throw new GlobalException("user token값을 함께 넘겨주세요.");
        }
        String userEmail = tokenService.getUserEmail(httpServletRequest);
        User user = userRepository.findUserByEmail(userEmail);
        Team team = teamRepository.findByTeamIdx(req.getTeamIdx());
        if(team == null){
            throw new NotFoundException("해당 team이 존재하지 않습니다.");
        }

        Plan plan = Plan.builder()
                .team(team)
                .user(user)
                .title(req.getTitle())

                .date(req.getDate())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())

                .location(req.getLocation())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .cost(req.getCost())
                .isModified(false)

                .build();

        if (req.getIsArchive() == 1){
            Archive archive = archiveRepository.findByArchiveIdx(req.getArchiveIdx());
            if(archive == null){
                throw new NotFoundException("해당 아카이브 게시물이 존재하지 않습니다.");
            }
            archive.activatePin();
        }

        return planRepository.save(plan).getPlanIdx();

    }

    @Transactional
    public PlanResponse.planDetailRes getPlanDetail(Long planIdx, HttpServletRequest clientToken){
        if(tokenService.getUserEmail(clientToken) == null){
            throw new GlobalException("올바른 user의 접근이 아닙니다.");
        }
        Plan plan = planRepository.findByPlanIdx(planIdx);
        if(plan == null){
            throw new GlobalException("해당 일정이 존재하지 않습니다.");
        }

        PlanResponse.planDetailRes dto = new PlanResponse.planDetailRes(
                plan.getPlanIdx(),
                plan.getTeam().getTeamIdx(),
                plan.getDate(),
                plan.dayOfDate(plan.getDate()),
                plan.getTitle(),
                String.valueOf(plan.getStartTime()),
                String.valueOf(plan.getEndTime()),
                plan.getLocation(),
                plan.getLatitude(),
                plan.getLongitude(),
                plan.getCost(),
                plan.getUser().getName(),
                plan.isModified()
        );

        return dto;
    }

    @Transactional
    public PlanResponse.planListRes getPlanList(Long teamIdx){
        //Team 정보 가져오기
        Team team = teamRepository.findByTeamIdx(teamIdx);
        if(team == null){
            throw new NotFoundException("해당 team이 존재하지 않습니다.");
        }

        TeamResponse.teamResponse teamDetail = new TeamResponse.teamResponse(
                team.getTeamIdx(),
                team.getTitle(),
                team.getStartDate(),
                team.getEndDate(),
                team.getImgUrl()
        );

        //Team Member List 가져오기
        List<TeamResponse.getMemberList> memberList = teamService.getMembers(teamIdx);

        //Plan List 가져오기
//        int planSize = planRepository.countByTeamIdx(teamIdx);
//        if(planSize == 0){
//            throw new GlobalException("해당 팀의 일정이 하나도 없습니다.");
//        }
        List<Plan> planList = planRepository.findAllByTeamIdxOrderByDateAndStartTime(teamIdx);


        List<PlanResponse.planDetailRes> detailListDto = planList.stream().map(plan -> new PlanResponse.planDetailRes(
                        plan.getPlanIdx(),
                        plan.getTeam().getTeamIdx(),
                        plan.getDate(),
                        plan.dayOfDate(plan.getDate()),
                        plan.getTitle(),
                        String.valueOf(plan.getStartTime()),
                        String.valueOf(plan.getEndTime()),
                        plan.getLocation(),
                        plan.getLatitude(),
                        plan.getLongitude(),
                        plan.getCost(),
                        plan.getUser().getName(),
                        plan.isModified()
                )
        ).collect(Collectors.toList());

        List<List> allPlanList = new ArrayList<>();

        if(detailListDto.size()==0){
            detailListDto = null;
        }

        else {

            List<PlanResponse.planDetailRes> planListByDate = new ArrayList<>();
            LocalDate currentDate = detailListDto.get(0).getDate();
            for (int i = 0; i < detailListDto.size(); i++) {
                if (detailListDto.get(i).getDate().equals(currentDate)) {
                    planListByDate.add(detailListDto.get(i));
                } else {
                    allPlanList.add(planListByDate);
                    currentDate = detailListDto.get(i).getDate();
                    planListByDate = new ArrayList<>();
                    planListByDate.add(detailListDto.get(i));
                }
            }
            allPlanList.add(planListByDate);
        }
        
        PlanResponse.planListRes planListRes = new PlanResponse.planListRes(teamDetail, memberList, allPlanList);
        return planListRes;
    }

    @Transactional
    public Long deletePlan(Long planIdx) throws EmptyResultDataAccessException {
        planRepository.deleteById(planIdx); return planIdx;
    }

    @Transactional
    public void updatePlan(Long planIdx, PlanRequest.updateReq req, HttpServletRequest httpServletRequest){
        Plan plan = planRepository.findByPlanIdx(planIdx);
        if(plan == null){
            throw new BadRequestException("해당 일정이 존재하지 않습니다.");
        }
        String userEmail = tokenService.getUserEmail(httpServletRequest);
        User user = userRepository.findUserByEmail(userEmail);
        plan.updatePlan(user,
                req.getTitle(),
                req.getDate(),
                req.getStartTime(),
                req.getEndTime(),
                req.getLocation(),
                req.getLatitude(),
                req.getLongitude(),
                req.getCost());
    }

    @Transactional
    public List<MapResponse.MapByDate> getMaps(Long teamIdx){
        Team team = teamRepository.findByTeamIdx(teamIdx);
        if(team == null){
            throw new NotFoundException("해당 team이 존재하지 않습니다.");
        }

        List<Plan> pointListEntity = planRepository.findAllByTeamIdxOrderByDateAndStartTime(teamIdx);
        List<MapResponse.Point> pointListDto = pointListEntity.stream().map(point -> new MapResponse.Point(
                point.getDate(),
                point.getTitle(),
                point.getLatitude(),
                point.getLongitude()
        )).collect(Collectors.toList());

        List<MapResponse.Point> allPointList = new ArrayList<>(); //Point List
        MapResponse.MapByDate result = new MapResponse.MapByDate(); //result
        List<MapResponse.MapByDate> listRes = new ArrayList<>(); //

        if(pointListDto.size()==0){
            pointListDto = null;
        }

        else {
            //List에 MapByDate를 하나씩 넣기
            //Date 안에 Point List들 존재
            //그 Date들을 List로 묶어서 보내기

            //1. pointListDto에서 하나씩 꺼내서 Date별로 모으기
            LocalDate currentDate = pointListDto.get(0).getDate();
            for (int i = 0; i < pointListDto.size(); i++) {
                if(currentDate.isEqual(pointListDto.get(i).getDate())){
                    allPointList.add(new MapResponse.Point(pointListDto.get(i).getDate(), pointListDto.get(i).getTitle(), pointListDto.get(i).getLatitude(), pointListDto.get(i).getLongitude()));
                }else{
                    result = new MapResponse.MapByDate(currentDate, allPointList);
                    listRes.add(result);

                    currentDate = pointListDto.get(i).getDate();
                    allPointList = new ArrayList<>();
                    allPointList.add(new MapResponse.Point(pointListDto.get(i).getDate(), pointListDto.get(i).getTitle(), pointListDto.get(i).getLatitude(), pointListDto.get(i).getLongitude()));
                }

            }
            result = new MapResponse.MapByDate(currentDate, allPointList);
            listRes.add(result);
        }



        return listRes;
    }

}