package com.weave.weaveserver.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor
@Entity
@Table(name = "belong")
public class Belong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int belongIdx;

    @OneToMany
    @JoinColumn(name = "user_idx")
    private User user;

    private int team;
}
