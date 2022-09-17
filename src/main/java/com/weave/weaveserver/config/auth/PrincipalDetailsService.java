package com.weave.weaveserver.config.auth;

import com.weave.weaveserver.domain.User;
import com.weave.weaveserver.repository.UserRepository;
import com.weave.weaveserver.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


// 시큐리티 설정에서 loginProcessingUrl("/login")
// http://localhost:8765/login 여기로 연결되는데
// Loginform.disable 해놔서 동작안함 => 필터 생성해줘야함JwtAthenticationFilter
// "/login" 요청이 오면 자동으로 UserDetailsService 타입으로 IoC되어 있는 loadUserByUsername 함수가 실행됨
//@RequiredArgsConstructor // final 사용하도록 . 의존성 바로 주입!
@Slf4j
@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
//    private UserService userService;
    private UserRepository userRepository;

    // 시큐리티 session(내부 Authentication 객체(내부 UserDeails)) 구조로 들어가게된다
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("username : "+username);
//        User user = userService.getUserByEmail(username);
        User user = userRepository.findUserByEmail(username);

        if(user != null) {
            System.out.println(user);
            return new PrincipalDetails(user);
        }
        System.out.println("user null");
        return null;
    }
}
