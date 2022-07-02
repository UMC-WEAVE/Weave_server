package com.weave.weaveserver.controller;

import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.dto.PlanRequest;
import com.weave.weaveserver.dto.PlanResponse;
import com.weave.weaveserver.dto.UserRequest;
import com.weave.weaveserver.service.PlanService;
import com.weave.weaveserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;

    //일정 추가
    @PostMapping("/plan")
    public ResponseEntity<?> createPlan(@RequestBody PlanRequest.createReq req){
        planService.addPlan(req);
        return ResponseEntity.ok(new JsonResponse(200, "addPlan",null));
    }

    //일정 상세 조회
    @GetMapping("/plan/{planIdx}")
    public ResponseEntity<?> getPlan(@PathVariable int planIdx){
        PlanResponse.planRes dto = planService.getPlan(planIdx);
        return ResponseEntity.ok(new JsonResponse(200, "getPlan", dto));
    }

    //해당 팀의 일정 리스트 조회
    @GetMapping("/team/{teamIdx}/plan")
    public ResponseEntity<?> getPlanList(@PathVariable int teamIdx){
        List<PlanResponse.planRes> planList = planService.getPlanList(teamIdx);
        return ResponseEntity.ok(new JsonResponse(200, "getPlanList", planList));
    }

    //일정 삭제
    @DeleteMapping("/plan/{planIdx}")
    public ResponseEntity<?> deletePlan(@PathVariable int planIdx){
        planService.deletePlan(planIdx);
        return ResponseEntity.ok(new JsonResponse(200, "deletePlan", null));
    }

    @PatchMapping("/plan/{planIdx}")
    public ResponseEntity<?> updatePlan(@PathVariable int planIdx, @RequestBody PlanRequest.createReq req){
        planService.updatePlan(planIdx, req);

        return ResponseEntity.ok(new JsonResponse(200, "updatePlan", null));
    }

}
