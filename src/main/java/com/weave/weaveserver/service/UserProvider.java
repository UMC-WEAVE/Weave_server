package com.weave.weaveserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weave.weaveserver.config.andOauth.GetKakao;
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
    public void deleteUser(String email) {
        User user;
        //TODO : test 12번 유저
        try {
            user = userRepository.findUserByEmail(email);
        }catch (NullPointerException e){
            throw new BadRequestException("이미 삭제된 유저");
        }

        try{
            teamService.deleteTeamByLeaderIdx(user.getUserIdx());
            planService.deleteAuthorByUserIdx(user.getUserIdx());
            archiveService.deleteAllArchiveByUserIdx(user);
            belongRepository.deleteByUser(user);
            userRepository.deleteById(user.getUserIdx());

        }catch (BadRequestException e){
            System.out.println("등록되지 않은 팀이래요~~");
        }
        userRepository.delete(user);
        String accessToken = getAcTokenByRefToken(user.getOauthToken(),user.getLoginId());
        unlinkKakao(accessToken);

        System.out.println(user.getUserIdx()+"번째 유저 삭제 완료");
    }

    public String getAcTokenByRefToken(String refreshToken, String loginId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> response;


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

            GetKakao kakaoProfile = null;
            try {
                kakaoProfile = objectMapper.readValue(response.getBody(), GetKakao.class);
            } catch (JsonProcessingException e) {
                log.info("[REJECT]kakaoMapper error");
            }
            return kakaoProfile.getAccess_token();
        }
        throw new BadRequestException("잘못된 플랫폼입니다.");
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
            throw new UnAuthorizedException("kakaoUnlink fail");
        }
        System.out.println(response);
    }
}
