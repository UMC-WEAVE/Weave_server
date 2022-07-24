package com.weave.weaveserver.config.oauth;


import com.weave.weaveserver.domain.Image;
import com.weave.weaveserver.domain.User;
import com.weave.weaveserver.repository.ImageRepository;
import com.weave.weaveserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);

        User findTestUser = userRepository.findUserByEmail(attributes.getEmail());

        System.out.println(findTestUser.getUserIdx()+"   "+findTestUser.getEmail());
//        httpSession.setAttribute("user", new SessionUser(user));
        System.out.println("loadUser http");
        return new DefaultOAuth2User(
//                Collections.singleton(new SimpleGrantedAuthority(user.getRole())),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private User saveOrUpdate(OAuthAttributes attributes){
        User user = userRepository.findUserByEmail(attributes.getEmail());

        if(user==null){
            user = new User();
            user.setLogin(attributes.getName(), attributes.getEmail(), attributes.getLoginId());
                Image image = new Image(attributes.getImage());
                Image imageSeq = imageRepository.save(image);
                System.out.println(image+"");
                user.setImageIdx(imageSeq);
        }else{
            log.info("이미 등록된 유저입니다.");
            if(user.getImageIdx()==null){
                Image image = new Image(attributes.getImage());
                Image imageSeq = imageRepository.save(image);
                System.out.println(image+"");
                user.setImageIdx(imageSeq);
                userRepository.save(user);
            }
            throw new IllegalArgumentException("이미 등록된 유저입니다.");
        }
        userRepository.save(user);
        System.out.println("saveOrUpdate 탐!!" + user);
        return user;
    }
}
