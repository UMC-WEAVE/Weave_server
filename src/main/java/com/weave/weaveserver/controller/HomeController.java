package com.weave.weaveserver.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.weave.weaveserver.service.FireBaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Controller
public class HomeController {
    @RequestMapping("/")
    public ModelAndView goHome() {
        ModelAndView mav = new ModelAndView("content/home");

        return mav;
    }
}
