package com.weave.weaveserver.controller;

import com.weave.weaveserver.config.exception.BadRequestException;
import com.weave.weaveserver.config.exception.GlobalException;
import com.weave.weaveserver.config.jwt.TokenService;
import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.dto.PlanRequest;
import com.weave.weaveserver.dto.PlanResponse;
import com.weave.weaveserver.dto.TeamResponse;
import com.weave.weaveserver.service.PlanService;
import com.weave.weaveserver.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;
    private final TokenService tokenService;
    private final TeamService teamService;

    @GetMapping("/plan/hello")
    public String hello(){
        log.info("log를 log 찍어보기");
        log.error("log가 에러라면");
        return "hello 은서 배포 완료";
    }


    //일정 추가
    @PostMapping("/plans")
    public ResponseEntity createPlan(@RequestBody PlanRequest.createReq req, HttpServletRequest httpServletRequest){
        log.info("[API] createPlan : createPlan API");

        //user token값 없을 때
        if(httpServletRequest == null){
            log.info("[REJECT] createPlan : user token 값 없음");
            throw new GlobalException("user token값을 함께 넘겨주세요.");
        }
        //team Idx가 없을 때
        if(req.getTeamIdx() == null){
            log.info("[REJECT] createPlan : team index 값 없음");
            throw new BadRequestException("teamIdx가 필요합니다.");
        }
        //title이 없을 때
        if(req.getTitle() == null){
            log.info("[REJECT] createPlan : title 값 없음");
            throw new BadRequestException("title을 입력해주세요.");
        }
        //date가 없을 때
        if(req.getDate() == null){
            log.info("[REJECT] createPlan : 일정 날짜 값 없음");
            throw new BadRequestException("일정 생성할 날짜를 입력해주세요.");
        }
        //startTime이 없을 때
        if(req.getStartTime() == null){
            log.info("[REJECT] createPlan : 일정 시작 시간 값 없음");
            throw new BadRequestException("생성할 일자의 시작 시간을 입력해주세요.");
        }

        //Archive에서 일정 생성 시 archive Idx 값을 같이 넘겨주지 않을 때
        if(req.getIsArchive() == 1){
            if(req.getArchiveIdx() == null){
                log.info("[REJECT] createPlan : archive index 값 없음");
                throw new BadRequestException("archiveIdx가 필요합니다.");
            }
        }

        if(httpServletRequest == null){
            log.info("[REJECT] addPlan : user token 값이 없습니다.");
            throw new GlobalException("user token값을 함께 넘겨주세요.");
        }
        String userEmail = tokenService.getUserEmail(httpServletRequest);
        Long planIdx = planService.addPlan(req, userEmail);

        return ResponseEntity.ok(new JsonResponse(201, "addPlan", planIdx));
    }

    //일정 상세 조회 (for Android)
    @GetMapping("/plans/{planIdx}")
    public ResponseEntity<?> getPlanDetail(@PathVariable Long planIdx, HttpServletRequest httpServletRequest){
        log.info("[API] getPlanDetail : getPlanDetailByPlanIdx");

        if(tokenService.getUserEmail(httpServletRequest) == null){
            log.info("[REJECT] getPlanDetail : user token값이 없습니다.");
            throw new GlobalException("올바른 user의 접근이 아닙니다.");
        }
        PlanResponse.planDetailRes res = planService.getPlanDetail(planIdx);
        return ResponseEntity.ok(new JsonResponse(200, "getPlan", res));
    }

    //해당 팀의 일정 리스트 조회 (Android & web)
    @GetMapping("/teams/{teamIdx}/plans")
    public ResponseEntity<?> getPlanList(@PathVariable Long teamIdx){
        log.info("[API] getPlanList : getPlanListByTeam");

        List<TeamResponse.getMemberList> memberList = teamService.getMembers(teamIdx);
        PlanResponse.planListRes res = planService.getPlanList(teamIdx, memberList);
        return ResponseEntity.ok(new JsonResponse(200, "getPlanList", res));
    }

    //일정 삭제
    @DeleteMapping("/plans/{planIdx}")
    public ResponseEntity deletePlan(@PathVariable Long planIdx){
        log.info("[API] deletePlan : deletePlanByPlanIdx");

        Long deletedPlanIdx = planService.deletePlan(planIdx);
        return ResponseEntity.ok(new JsonResponse(200, "deletePlan", deletedPlanIdx));
    }

    //일정 수정
    @PatchMapping("/plans/{planIdx}")
    public ResponseEntity<?> updatePlan(@PathVariable Long planIdx, @RequestBody PlanRequest.updateReq req, HttpServletRequest httpServletRequest){
        log.info("[API] updatePlan : updatePlan API");

        String userEmail = tokenService.getUserEmail(httpServletRequest);
        planService.updatePlan(planIdx, req, userEmail);
        return ResponseEntity.ok(new JsonResponse(201, "updatePlan", null));
    }


    //일정 삭제
    @DeleteMapping("/teams/{teamIdx}/plans")
    public ResponseEntity deleteAllPlan(@PathVariable Long teamIdx){
        log.info("[API] deleteAllPlan : deleteAllPlanByTeamIdx");

        planService.deleteAllPlan(teamIdx);
        return ResponseEntity.ok(new JsonResponse(200, "deleteAllPlanByTeamIdx", null));
    }

}