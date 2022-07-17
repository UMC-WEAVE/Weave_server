package com.weave.weaveserver.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@Getter
@Builder
@Table(name = "user")
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_idx")
    private int userIdx;

    @Column(name = "refresh_token")
    private String refreshToken;

    private String name;

    private String email;

    @Column(name = "login_id")
    private String loginId;

    private String role;

}
