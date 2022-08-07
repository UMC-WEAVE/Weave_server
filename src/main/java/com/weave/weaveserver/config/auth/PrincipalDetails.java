package com.weave.weaveserver.config.auth;

import com.weave.weaveserver.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

// 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
//로그인 진행이 완료가 되면 시큐리티 session을 만들어준다.(Security ContextHolder)
// Object 타입 => Authentication 타입 객체
//Authentication 안에 User 정보가 있어야 됨
//User Object 타입 => UserDetails 타입 객체

//Security Session => Authentication => UserDetails(PrincipalDetails)


public class PrincipalDetails implements UserDetails, OAuth2User {

    private User user;    //콤포지션


    //해당 User의 권한을 반환하는 곳!!
    //일반 로그인
    public PrincipalDetails(User user) {
        this.user = user;
    }

    //소셜 로그인
    public PrincipalDetails(User user,  Map<String ,Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    private Map<String ,Object> attributes;

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collet = new ArrayList<GrantedAuthority>();
        collet.add(()->{ return "ROLE_USER";});
        return collet;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        // 계정이 잠겼을 때 사용하는 메소드
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        //사이트에서 1년간 회원 활동이 없을 때 휴면 계정 전환 => recDate랑 비교해서 넣기
//        user.getRecDate()
        return true;
    }

    @Override
    public String getName() {
        return user.getEmail()+"";
    }
}
