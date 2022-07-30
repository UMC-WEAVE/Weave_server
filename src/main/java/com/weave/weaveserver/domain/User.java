package com.weave.weaveserver.domain;

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
    public User(String name, String email, String loginId) {
        this.name = name;
        this.email = email;
        this.loginId=loginId;
    }

    public void setLogin(String name, String email, String loginId){
        this.name=name;
        this.email=email;
        this.loginId=loginId;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
