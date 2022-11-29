package com.weave.weaveserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weave.weaveserver.config.andOauth.GetAcToken;
import com.weave.weaveserver.config.andOauth.SecurityProperties;
import com.weave.weaveserver.config.exception.BadRequestException;
import com.weave.weaveserver.config.exception.UnAuthorizedException;
import com.weave.weaveserver.domain.User;
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
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserProvider {
    private final UserRepository userRepository;
    private final PlanService planService;
    private final TeamService teamService;
    private final ArchiveService archiveService;
    private final BelongRepository belongRepository;
    private final ObjectMapper objectMapper;


    @Transactional
    public void deleteUser(String email, String loginId) {
        User user;
        //TODO : test 12번 유저
        try {
            user = userRepository.findUserByEmail(email);
        }catch (NullPointerException e){
            throw new BadRequestException("이미 삭제된 유저");
        }

        try{
            teamService.deleteBelongTeam(user.getEmail());
            planService.deleteAuthorByUserIdx(user.getUserIdx());
            archiveService.deleteAllArchiveByUserIdx(user);
            if(belongRepository.countTeamByUser(user.getUserIdx())>0){
                belongRepository.deleteByUser(user);
            }
//            userRepository.deleteById(user.getUserIdx());

        }catch (NullPointerException e){
            System.out.println("deleteUser error");
        }

//        userRepository.delete(user);

        String refToken = getAcTokenByRefToken(user.getOauthToken(),user.getLoginId());


        switch (loginId){
            case "kakao":
                unlinkKakao(refToken);break;
            case "naver":
                unlinkNaver(refToken);break;
        }

        System.out.println(user.getUserIdx()+"번째 유저 삭제 완료");
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

            System.out.println(response);

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
        System.out.println(response);
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
        params.add("client_id", SecurityProperties.naver_client_id);
        params.add("client_secret", SecurityProperties.naver_client_secret);
        params.add("grant_type", "delete");
        params.add("access_token", accessToken);
        params.add("service_provider", "NAVER");


        HttpEntity<MultiValueMap<String, String>> naverRequest = new HttpEntity<>(params, headers);

        try {
            response = restTemplate.exchange(host, HttpMethod.GET, naverRequest, String.class);
        } catch (Exception e) {
            throw new BadRequestException("이미 만료된 사용자");
        }
        System.out.println(response);
    }
}
