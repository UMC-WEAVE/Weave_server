package com.weave.weaveserver.domain;

import com.weave.weaveserver.dto.UserRequest;
import com.weave.weaveserver.util.UuidUtil;
import lombok.*;

import javax.persistence.*;

@Getter
@Table(name = "user")
@NoArgsConstructor
@Entity
public class User extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_idx")
    private Long userIdx;

    @Column(nullable = false)
    private String uuid;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(name = "login_type",nullable = false)
    private String loginId;

    @Column(nullable = true)
    private String image;

    @Column(name = "refresh_token",nullable = false)
    private String oauthToken;



    @Builder
    public User(String name, String email, String loginId, String image, String oauthToken, String uuid) {
        this.name = name;
        this.email = email;
        this.loginId=loginId;
        this.image = image;
        this.uuid = uuid;
        this.oauthToken = oauthToken;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static User joinUser(UserRequest.join joinUser){
        return User.builder()
                .name(joinUser.getName()).email(joinUser.getEmail()).loginId(joinUser.getLoginId()).image(joinUser.getImage()).uuid(UuidUtil.generateType1UUID())
                .oauthToken(joinUser.getOauthToken()).build();
    }

    public void setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }
}