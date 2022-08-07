package com.weave.weaveserver.config.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weave.weaveserver.config.jwt.Token;
import com.weave.weaveserver.config.jwt.TokenService;
import com.weave.weaveserver.dto.JsonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenService tokenService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        Token token = tokenService.generateToken(email);

        System.out.println(token.getToken());

        String targetUrl = UriComponentsBuilder.fromUriString("/token")
                .queryParam("token", token.getToken())
                .build().toUriString();
//        JsonResponse data = new JsonResponse(200,"login",token);
//        String result = objectMapper.writeValueAsString("data");
//        response.getWriter().write(result);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}