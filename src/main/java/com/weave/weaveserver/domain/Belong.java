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
    @Column(name = "belong_idx")
    private int belongIdx;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

//    @Column(name = "user_idx")
//    private int userIdx;

    @ManyToOne
    @JoinColumn(name = "team_idx")
    private Team team;
}
