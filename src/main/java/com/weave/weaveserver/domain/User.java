package com.weave.weaveserver.domain;

import com.weave.weaveserver.dto.UserRequest;
import lombok.*;

import javax.persistence.*;

@Getter
@Table(name = "user")
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_idx")
    private Long userIdx;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(name = "login_type",nullable = false)
    private String loginId;


    @Column(nullable = true)
    private String image;

    @Builder
    public User(String name, String email, String loginId, String image) {
        this.name = name;
        this.email = email;
        this.loginId=loginId;
        this.image = image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static User joinUser(UserRequest.join joinUser){
        return User.builder()
                .name(joinUser.getName()).email(joinUser.getEmail()).loginId(joinUser.getLoginId()).image(joinUser.getImage())
                .build();
    }

}