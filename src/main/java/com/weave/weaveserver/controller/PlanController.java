package com.weave.weaveserver.controller;

import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.dto.PlanRequest;
import com.weave.weaveserver.dto.PlanResponse;
import com.weave.weaveserver.dto.UserRequest;
import com.weave.weaveserver.service.PlanService;
import com.weave.weaveserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;

    @PostMapping("/plan")
    public ResponseEntity<?> createPlan(@RequestBody PlanRequest.createReq req){
        planService.addPlan(req);
        return ResponseEntity.ok(new JsonResponse(200, "addPlan",null));
    }

    @GetMapping("/plan/{planIdx}")
    public ResponseEntity<?> getPlan(@PathVariable int planIdx){
        PlanResponse.planRes dto = planService.getPlan(planIdx);
        return ResponseEntity.ok(new JsonResponse(200, "getPlan", dto));
    }

    @GetMapping("/plan")
    public ResponseEntity<?> getPlanList(@RequestBody PlanRequest.getPlanList res){
        List<PlanResponse.planRes> planList = planService.getPlanList(res.getTeamIdx());
        return ResponseEntity.ok(new JsonResponse(200, "getPlanList", planList));
    }


}
