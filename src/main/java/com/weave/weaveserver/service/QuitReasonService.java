package com.weave.weaveserver.service;

import com.weave.weaveserver.domain.Reason;
import com.weave.weaveserver.dto.ReasonRequest;
import com.weave.weaveserver.repository.ReasonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuitReasonService {
    public final ReasonRepository reasonRepository;


    @Transactional
    public void addQuitReason(ReasonRequest reasons){
        log.info("[INFO] addQuitReason");

        Reason reasonEntity = Reason.builder()
                .item1(reasons.isItem1())
                .item2(reasons.isItem2())
                .item3(reasons.isItem3())
                .subItem(reasons.getSubItem())
                .build();

        reasonRepository.save(reasonEntity);
    }
}
