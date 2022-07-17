package com.weave.weaveserver.config.jwt;




public interface JwtProperties{
	String SECRET = "cos"; // 우리 서버만 알고 있는 비밀값
	int EXPIRATION_TIME = 30*60*1000; // 60분 (1/1000초)
	long REF_EXPIRATION_TIME = 7*24*60*60*1000L;//토큰 유효시간 7일
	int EMAIL_EXPIRATION_TIME = 5*60*1000;
	String TOKEN_PREFIX = "Bearer ";
	String ACCESS_HEADER_STRING = "X-AUTH-TOKEN";
	String REFRESH_HEADER_STRING = "X-AUTH-REF-TOKEN";
}
