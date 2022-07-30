package com.weave.weaveserver.controller;

import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.dto.PlanRequest;
import com.weave.weaveserver.dto.PlanResponse;
import com.weave.weaveserver.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;

    @GetMapping("/plan/hello")
    public String hello(){
        return "hello";
    }


    //일정 추가
    @PostMapping("/plans")
    public ResponseEntity createPlan(@RequestBody PlanRequest.createReq req){
        Long planIdx = planService.addPlan(req);
        if(req.getIsArchive() == 1){
            if(req.getArchiveIdx() == null){
                return ResponseEntity.badRequest().body(new JsonResponse(4444, "archiveIdx가 꼭 있어야합니다!!", null));
            }
        }
        return ResponseEntity.ok(new JsonResponse(201, "addPlan", planIdx));
    }

    //일정 상세 조회 (for Android)
    @GetMapping("/plans/{planIdx}")
    public ResponseEntity<?> getPlanDetail(@PathVariable Long planIdx){
        PlanResponse.planDetailRes res = planService.getPlanDetail(planIdx);
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
    public ResponseEntity<?> updatePlan(@PathVariable Long planIdx, @RequestBody PlanRequest.createReq req){
        planService.updatePlan(planIdx, req);

        return ResponseEntity.ok(new JsonResponse(200, "updatePlan", null));
    }

}
