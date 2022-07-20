package com.weave.weaveserver.controller;

import com.weave.weaveserver.domain.Plan;
import com.weave.weaveserver.dto.PlanRequest;
import com.weave.weaveserver.repository.PlanRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PlanControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PlanRepository planRepository;

    @After
    public void tearDown() throws Exception{
        planRepository.deleteAll();
    }

    @Test
    public void addPlan() throws Exception{
        //given
        PlanRequest.createReq reqDto = PlanRequest.createReq.builder()
                .teamIdx(1)
                .userIdx(1)
                .title("제주도 흑돼지")
                .date(LocalDate.now())
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .location("제주도 서귀포시 어쩌고 어쩌고")
                .latitude(43.5555)
                .longitude(45.6666)
                .cost(58000)
                .build();

        String url = "http://localhost:" + port + "/plan";

        //when
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, reqDto, Long.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Plan> all = planRepository.findAll();
//        assertThat(all.get(0).getTitle()).isEqualTo()


    }

}