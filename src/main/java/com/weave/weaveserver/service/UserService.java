package com.weave.weaveserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weave.weaveserver.config.andOauth.*;
import com.weave.weaveserver.config.exception.BadRequestException;
import com.weave.weaveserver.config.exception.LoginPlatformException;
import com.weave.weaveserver.config.exception.UnAuthorizedException;
import com.weave.weaveserver.config.jwt.Token;
import com.weave.weaveserver.config.jwt.TokenService;
import com.weave.weaveserver.domain.User;
import com.weave.weaveserver.dto.UserRequest;
import com.weave.weaveserver.dto.UserResponse;
import com.weave.weaveserver.repository.BelongRepository;
import com.weave.weaveserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BelongRepository belongRepository;
    private final PlanService planService;
    private final TeamService teamService;
    private final ArchiveService archiveService;
    private final ObjectMapper objectMapper;
    private final TokenService tokenService;
    private final UserProvider userProvider;

    @Transactional
    public String joinUser(UserRequest.join joinUser) {
        User user = User.joinUser(joinUser);
        System.out.println(""+user.getImage()+user.getName());
        userRepository.save(user);
        return user.getUuid();
    }

    @Transactional
    public UserResponse.myPage loadMyPage(String uuid) {
        User user = userRepository.findUserByUuid(uuid);
        Long countTeam = belongRepository.countTeamByUser(user.getUserIdx());
        //countTeamIdx
        return UserResponse.myPage.builder()
                .email(user.getEmail()).name(user.getName()).image(user.getImage()).countTeam(countTeam).loginType(user.getLoginId())
                .build();
    }


    @Transactional
    public String loginUser(User user, String loginId) {
        if(!user.getLoginId().equals(loginId)) throw new LoginPlatformException(user.getLoginId());
        userRepository.save(user);
        return user.getUuid();
    }



    @Transactional
    public Token androidLogin(UserRequest.login login, String loginId){
        String refreshToken = login.getRefreshToken();
        String accessToken = login.getAccessToken();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
        ResponseEntity<String> response;
        headers.add("Authorization", "Bearer " + accessToken);

        String email="";
        String uuid ="";
        String redirect_uri="";
        KakaoProfile kakaoProfile = null;
        GoogleProfile googleProfile = null;
        NaverProfile naverProfile=null;
        Token token;

        UserRequest.join joinUser;

        log.info("[API]"+loginId+"Login");

        if(loginId.equals("kakao")) {
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
            redirect_uri="https://kapi.kakao.com/v2/user/me";

            try{
                response=restTemplate.exchange(redirect_uri, HttpMethod.POST,request,String.class);
                kakaoProfile = objectMapper.readValue(response.getBody(), KakaoProfile.class);
            }catch (HttpClientErrorException e){
                log.info("[REJECT]kakao login error");
                throw new BadRequestException("[REJECT]kakao login error");
            }catch (JsonProcessingException e){
                log.info("[REJECT]kakaoMapper error");
            }

            email=kakaoProfile.getKakao_account().getEmail();
            joinUser = UserRequest.join.builder()
                    .email(email)
                    .loginId("kakao")
                    .name(kakaoProfile.getProperties().getNickname())
                    .image(kakaoProfile.getProperties().getThumbnail_image())
                    .oauthToken(refreshToken).build();
//            System.out.println(response.getBody());

            User user = userProvider.getUserByEmail(email);
            if(user==null){
                log.info("kakao join : "+email);
                uuid = joinUser(joinUser);
            }else{
                user.setOauthToken(refreshToken);
                uuid = loginUser(user, loginId);
                log.info("kakao login : "+email);
            }
        } else if(loginId.equals("naver")) {
            redirect_uri="https://openapi.naver.com/v1/nid/me";
            try {
                response=restTemplate.exchange(redirect_uri, HttpMethod.POST,request,String.class);
                naverProfile = objectMapper.readValue(response.getBody(), NaverProfile.class);
            }catch (HttpClientErrorException e){
                log.info("[REJECT]naver login error");
                throw new BadRequestException("[REJECT]naver login error");
            } catch (JsonProcessingException e) {
                log.info("[REJECT]naverMapper error");
            }
            email=naverProfile.getResponse().getEmail();
            joinUser = UserRequest.join.builder()
                    .email(email)
                    .loginId("naver")
                    .name(naverProfile.getResponse().getName())
                    .image(naverProfile.getResponse().getProfile_image())
                    .oauthToken(refreshToken).build();
//            System.out.println("response.getBody() = " + response.getBody());

            User user = userProvider.getUserByEmail(email);
            if(user==null){
                log.info("naver join : "+email);
                uuid = joinUser(joinUser);
            }else{
                user.setOauthToken(refreshToken);
                uuid = loginUser(user, loginId);
                log.info("naver login : "+email);
            }
        } else if(loginId.equals("google")){
            redirect_uri="https://www.googleapis.com/oauth2/v1/userinfo";
            try {
                response=restTemplate.exchange(redirect_uri, HttpMethod.GET,request,String.class);
                googleProfile = objectMapper.readValue(response.getBody(), GoogleProfile.class);
            }catch (HttpClientErrorException e){
                log.info("[REJECT]google login error");
                log.info(e.getMessage());
                throw new BadRequestException("[REJECT]google login error");
            } catch (JsonProcessingException e) {
                log.error("[REJECT]googleMapper error");
            }
            email= googleProfile.getEmail();
            System.out.println(email + "구글이메일임");

            System.out.println("login.getRefreshToken() : "+login.getRefreshToken());



            User user = userProvider.getUserByEmail(email);


            if(login.getRefreshToken()==null){
                refreshToken = user.getOauthToken();
            }


            joinUser = UserRequest.join.builder()
                    .email(email)
                    .loginId("google")
                    .name(googleProfile.getName())
                    .image(googleProfile.getPicture())
                    .oauthToken(refreshToken).build();

            System.out.println(joinUser.toString());
            if(user==null){
                log.info("google join : "+email);
                //회원가입 시에는 refresh token이 필수임
                if(joinUser.getOauthToken()==null){
                    log.info("[REJECT]join -> refresh token is null ");
                    throw new BadRequestException("구글 회원가입 실패");
                }
                uuid = joinUser(joinUser);
            }else{
                if(refreshToken!=null){
                    user.setOauthToken(refreshToken);
                }
                uuid = loginUser(user, loginId);
                log.info("google login : "+email);
            }

        }else{
            log.info("[REJECT]wrong platform");
            throw new BadRequestException("잘못된 플랫폼으로 접근");
        }

        token = tokenService.generateToken(uuid);
        token.setLoginId(loginId);

        return token;
    }



    @Transactional
    public void deleteUser(String uuid, String loginId) {
        User user;
        //TODO : test 12번 유저
        try {
            user = userRepository.findUserByUuid(uuid);
        }catch (NullPointerException e){
            throw new BadRequestException("이미 삭제된 유저");
        }

        try{
            teamService.deleteBelongTeam(user);
            planService.deleteAuthorByUserIdx(user.getUserIdx());
            archiveService.setUserNullByUser(user);
            if(belongRepository.countTeamByUser(user.getUserIdx())>0){
                belongRepository.deleteByUser(user);
            }
//            userRepository.deleteById(user.getUserIdx());


            userRepository.delete(user);

        }catch (NullPointerException e){
            System.out.println("deleteUser error");
        }

        String acToken = getAcTokenByRefToken(user.getOauthToken(),user.getLoginId());

        switch (loginId){
            case "kakao":
                unlinkKakao(acToken);break;
            case "naver":
                unlinkNaver(acToken);break;
            case "google":
                unlinkGoogle(acToken);break;
        }

        System.out.println(user.getEmail()+" 유저 삭제 완료");
    }

    public String getAcTokenByRefToken(String refreshToken, String loginId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> response;


        //unlink kakao
        if (loginId.equals("kakao")) {

            String host = "https://kauth.kakao.com/oauth/token";
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "refresh_token");
            params.add("client_id", SecurityProperties.kakao_client_id);
            params.add("refresh_token", refreshToken);

            HttpEntity<MultiValueMap<String, String>> kakaoRequest = new HttpEntity<>(params, headers);

            try {
                response = restTemplate.exchange(host, HttpMethod.POST, kakaoRequest, String.class);
            } catch (Exception e) {
                throw new BadRequestException("이미 만료된 사용자");
            }

            GetAcToken kakaoProfile = null;
            try {
                kakaoProfile = objectMapper.readValue(response.getBody(), GetAcToken.class);
            } catch (JsonProcessingException e) {
                log.info("[REJECT]kakaoMapper error");
            }
            return kakaoProfile.getAccess_token();
        }

        //unlink naver
        if(loginId.equals("naver")){

            System.out.println("getAcTokenByRefToken");

            String host = "https://nid.naver.com/oauth2.0/token";

            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", SecurityProperties.naver_client_id);
            params.add("client_secret", SecurityProperties.naver_client_secret);
            params.add("grant_type", "refresh_token");
            params.add("refresh_token", refreshToken);

            HttpEntity<MultiValueMap<String, String>> naverRequest = new HttpEntity<>(params, headers);

            try {
                response = restTemplate.exchange(host, HttpMethod.POST, naverRequest, String.class);
            } catch (Exception e) {
                throw new BadRequestException("이미 만료된 사용자");
            }
            GetAcToken naverProfile = null;
            try {
                naverProfile = objectMapper.readValue(response.getBody(), GetAcToken.class);
            } catch (JsonProcessingException e) {
                log.info("[REJECT]naverMapper error");
            }
            return naverProfile.getAccess_token();
        }
        if(loginId.equals("google")){
            System.out.println("unlink google");

            String url = "https://www.googleapis.com/oauth2/v4/token";
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");


            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

            HttpEntity<MultiValueMap<String, String>> googleProfile = new HttpEntity<>(params, headers);

            params.add("client_id", SecurityProperties.google_client_id);
            params.add("client_secret", SecurityProperties.google_client_secret);
            params.add("refresh_token", refreshToken);
            params.add("grant_type", "refresh_token");

            try {
                response = restTemplate.exchange(url, HttpMethod.POST, googleProfile, String.class);
            } catch (Exception e) {
                throw new BadRequestException("이미 만료된 사용자");
            }

            GetGoogleAcToken googleAcToken = null;
            try {
                googleAcToken = objectMapper.readValue(response.getBody(), GetGoogleAcToken.class);
            } catch (JsonProcessingException e) {
                log.info("[REJECT]googleMapper error");
            }

//            String result = restTemplate.postForObject(url, params, String.class);
//            System.out.println("google actoken = "+googleAcToken.getAccess_token());
            return googleAcToken.getAccess_token();
        }
        throw new BadRequestException("[REJECT]unlink platform error.");
    }


    public void unlinkKakao(String accessToken){
        System.out.println("accessToken = "+accessToken);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
        ResponseEntity<String> response;

        String host = "https://kapi.kakao.com/v1/user/unlink";
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

//            headers.add("Authorization", "KakaoAK " + kakaoProfile.getAccess_token());

        try{
            response=restTemplate.exchange(host, HttpMethod.POST,request,String.class);
        }catch (Exception e){
            throw new UnAuthorizedException("[REJECT]kakaoUnlink fail");
        }
    }

    public void unlinkNaver(String accessToken){
        System.out.println("accessToken = "+accessToken);
        System.out.println("unlinkNaver");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> response;

        String host = "https://nid.naver.com/oauth2.0/token";

//        if(accessToken.replaceAll("+").){
//            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//        }

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "delete");
        params.add("client_id", SecurityProperties.naver_client_id);
        params.add("client_secret", SecurityProperties.naver_client_secret);
        params.add("access_token", accessToken);
        params.add("service_provider", "NAVER");


        HttpEntity<MultiValueMap<String, String>> naverRequest = new HttpEntity<>(params, headers);

        try {
            response = restTemplate.exchange(host, HttpMethod.POST, naverRequest, String.class);
        } catch (Exception e) {
            throw new BadRequestException("이미 만료된 사용자");
        }
    }

    public void unlinkGoogle(String accessToken){
        System.out.println("unlintk google");
        System.out.println("accessToken = "+accessToken);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> response;

        String host = "https://oauth2.googleapis.com/revoke";

        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");


        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("token", accessToken);



        HttpEntity<MultiValueMap<String, String>> naverRequest = new HttpEntity<>(params, headers);

        try {
            response = restTemplate.exchange(host, HttpMethod.POST, naverRequest, String.class);
            System.out.println(response);
        } catch (Exception e) {
            throw new BadRequestException("이미 만료된 사용자");
        }
    }


}
