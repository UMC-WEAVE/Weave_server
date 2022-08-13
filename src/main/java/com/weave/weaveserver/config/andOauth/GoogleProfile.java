package com.weave.weaveserver.config.andOauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Data
//구글(서드파티)로 액세스 토큰을 보내 받아올 구글에 등록된 사용자 정보
public class GoogleProfile {
    public String id;
    public String email;
    public Boolean verifiedEmail;
    public String name;
    public String givenName;
    public String familyName;
    public String picture;
    public String locale;
}