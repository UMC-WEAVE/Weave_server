package com.weave.weaveserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weave.weaveserver.config.exception.jwt.JwtAuthenticationEntryPoint;
import com.weave.weaveserver.config.exception.jwt.JwtExceptionFilter;
import com.weave.weaveserver.config.jwt.JwtAuthenticationFilter;
import com.weave.weaveserver.config.jwt.TokenService;
import com.weave.weaveserver.config.oauth.CustomOAuth2UserService;
import com.weave.weaveserver.config.oauth.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//http://localhost:8080/oauth2/authorization/kakao

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().antMatchers("/").antMatchers("/resources/**");
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable();
        http.formLogin().disable();
        http.csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                .antMatchers("/login/**").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/token/**").permitAll()
                .antMatchers("/image/**").permitAll()
                .antMatchers("/android/login/**").permitAll()
                .antMatchers("/user/**").access("hasRole('ROLE_USER')")
//                .antMatchers("/swagger-ui/**").permitAll()
//                .antMatchers("/gcp/**").permitAll()
//                .antMatchers("/test/**").permitAll()
                .antMatchers("/service").permitAll()
                .antMatchers("/manual").permitAll()
                .antMatchers("/health").permitAll()
                .anyRequest().access("hasRole('ROLE_USER')")
                .and()
                .logout()
                .logoutSuccessUrl("/");

        http.exceptionHandling().authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(tokenService), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtExceptionFilter(objectMapper),JwtAuthenticationFilter.class)
                //JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter전 넣는다.
                .logout().permitAll();
    }
}
