package com.weave.weaveserver.config;

import com.weave.weaveserver.config.oauth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//http://localhost:8080/oauth2/authorization/kakao

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                .antMatchers("/android").permitAll()
                .antMatchers("/gallery").permitAll()
                .anyRequest().permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorize")
//                .authorizationRequestRepository(cookieAuthRepositories())
                .and()
                .redirectionEndpoint()
                .baseUri("/oauth/login/oauth2/code/**")
                .and()
                .userInfoEndpoint()
                .userService(customOAuth2UserService);
    }
}
