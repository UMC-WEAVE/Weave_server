package com.weave.weaveserver.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@Getter
@Builder
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userIdx;

    private String refreshToken;

    private String name;

    private String email;

    private String loginId;


}
