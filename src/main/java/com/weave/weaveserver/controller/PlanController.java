package com.weave.weaveserver.controller;

import com.weave.weaveserver.config.exception.BadRequestException;
import com.weave.weaveserver.config.exception.GlobalException;
import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.dto.PlanRequest;
import com.weave.weaveserver.dto.PlanResponse;
import com.weave.weaveserver.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;

    @GetMapping("/plan/hello")
    public String hello(){
        return "hello 은서 배포 완료";
    }


    //일정 추가
    @PostMapping("/plans")
    public ResponseEntity createPlan(@RequestBody PlanRequest.createReq req, HttpServletRequest httpServletRequest){
        //user token값 없을 때
        if(httpServletRequest == null){
            throw new GlobalException("user token값을 함께 넘겨주세요.");
        }
        //team Idx가 없을 때
        if(req.getTeamIdx() == null){
            throw new BadRequestException("teamIdx가 필요합니다.");
        }
        //title이 없을 때
        if(req.getTitle() == null){
            throw new BadRequestException("title을 입력해주세요.");
        }
        //date가 없을 때
        if(req.getDate() == null){
            throw new BadRequestException("일정 생성할 날짜를 입력해주세요.");
        }
        //startTime이 없을 때
        if(req.getStartTime() == null){
            throw new BadRequestException("생성할 일자의 시작 시간을 입력해주세요.");
        }

        //Archive에서 일정 생성 시 archive Idx 값을 같이 넘겨주지 않을 때
        if(req.getIsArchive() == 1){
            if(req.getArchiveIdx() == null){
                throw new BadRequestException("archiveIdx가 필요합니다.");
            }
        }
        Long planIdx = planService.addPlan(req, httpServletRequest);
        return ResponseEntity.ok(new JsonResponse(201, "addPlan", planIdx));
    }

    //일정 상세 조회 (for Android)
    @GetMapping("/plans/{planIdx}")
    public ResponseEntity<?> getPlanDetail(@PathVariable Long planIdx, HttpServletRequest httpServletRequest){
        PlanResponse.planDetailRes res = planService.getPlanDetail(planIdx, httpServletRequest);
        return ResponseEntity.ok(new JsonResponse(200, "getPlan", res));
    }

    //해당 팀의 일정 리스트 조회 (Android & web)
    @GetMapping("/teams/{teamIdx}/plans")
    public ResponseEntity<?> getPlanList(@PathVariable Long teamIdx){
        PlanResponse.planListRes res = planService.getPlanList(teamIdx);
        return ResponseEntity.ok(new JsonResponse(200, "getPlanList", res));
    }

    //일정 삭제
    @DeleteMapping("/plans/{planIdx}")
    public ResponseEntity deletePlan(@PathVariable Long planIdx){
        Long deletedPlanIdx = planService.deletePlan(planIdx);
        return ResponseEntity.ok(new JsonResponse(200, "deletePlan", deletedPlanIdx));
    }

    //일정 수정
    @PatchMapping("/plans/{planIdx}")
    public ResponseEntity<?> updatePlan(@PathVariable Long planIdx, @RequestBody PlanRequest.updateReq req, HttpServletRequest httpServletRequest){
        planService.updatePlan(planIdx, req, httpServletRequest);

        return ResponseEntity.ok(new JsonResponse(201, "updatePlan", null));
    }

}