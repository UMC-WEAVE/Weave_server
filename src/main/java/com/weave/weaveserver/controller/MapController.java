package com.weave.weaveserver.controller;

import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.dto.MapResponse;
import com.weave.weaveserver.dto.PlanResponse;
import com.weave.weaveserver.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MapController {
    final PlanService planService;

    @GetMapping("/teams/{teamIdx}/map")
    public ResponseEntity getMapList(@PathVariable Long teamIdx){
        List<MapResponse> pointList = planService.getMaps(teamIdx);
        return ResponseEntity.ok(new JsonResponse(200, "getMapPoints", pointList));
    }
}
