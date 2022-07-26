package com.weave.weaveserver.controller;

import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.weave.weaveserver.config.jwt.UserDto;
import com.weave.weaveserver.config.oauth.CustomOAuth2UserService;
import com.weave.weaveserver.domain.Image;
import com.weave.weaveserver.domain.Plan;
import com.weave.weaveserver.domain.Team;
import com.weave.weaveserver.domain.User;
import com.weave.weaveserver.dto.PlanRequest;
import com.weave.weaveserver.dto.PlanResponse;
import com.weave.weaveserver.repository.ImageRepository;
import com.weave.weaveserver.repository.PlanRepository;
import com.weave.weaveserver.repository.UserRepository;
import com.weave.weaveserver.service.PlanService;
import org.codehaus.jackson.map.ObjectMapper;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.InstanceOfAssertFactories.map;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@PropertySource("classpath:config.SecurityConfig")

@WebMvcTest(PlanController.class)
@WebAppConfiguration
public class PlanControllerTest {

    private MockMvc mockMvc;

    @MockBean
    CustomOAuth2UserService customOAuth2UserService;

//    @Autowired
    private ObjectMapper mapper = new ObjectMapper();


    @MockBean
    private PlanService planService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PlanController(planService))
                .addFilter(new CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))
                .alwaysDo(print())
                .build();
    }

    @Test
    @DisplayName("[API][POST] 일정 추가")
    public void plan_POST() throws Exception {
        ObjectNode content = mapper.createObjectNode();
        content.put("userIdx", 2);
        content.put("teamIdx", 2);
        content.put("title", "제주도 흑돼지");
        content.put("date", "");
        content.put("startTime", "");
        content.put("cost", 58000);



        //given
        Long teamIdx = content.get("teamIdx").getLongValue();
        given(planService.addPlan(any())).willReturn(teamIdx);

        //when
        final ResultActions actions =
                mockMvc.perform(
                        post("/plan")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(String.valueOf(content)));
        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(content.get("teamIdx").getLongValue()));

    }

}