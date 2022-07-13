package com.weave.weaveserver.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PlanControllerTest {

    @Autowired
    private PlanController planController;

    private MockMvc mockMvc;

    @Test
    public void hello(){
        System.out.println(planController.hello());
        assertThat(planController.hello()).isEqualTo("hello");
    }

}