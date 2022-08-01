package com.weave.weaveserver.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor
@Entity
@Table(name = "belong")
@NoArgsConstructor
public class Belong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long belongIdx;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_idx")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Team team;
}
