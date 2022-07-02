package com.weave.weaveserver.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration  //IoC 빈(bean)등록
@EnableWebSecurity //스프링 시큐리티 필터가 스프링 필터체인에 등록이 된다.
@RequiredArgsConstructor
//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // 특정 주소 접근시 권한 및 인증을 위한 어노테이션 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.csrf().disable();//rest api이므로 csrf 보안이 필요 없으므로  disable
        http
                .httpBasic().disable()  //Basic : cookie에 저장해서 접근 / Bearer : token으로 접근(유효시간)
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //session을 사용하지 않음
//                .and()
                .formLogin().disable()  //formlogin이나 기본 httplogin방식을 아예 쓰지 않는다.
                .authorizeRequests()    //요청에 대한 사용권한 체크
                .anyRequest().permitAll()
                .and()
                .logout().permitAll();
    }

}
