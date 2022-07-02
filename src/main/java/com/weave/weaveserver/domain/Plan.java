package com.weave.weaveserver.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Entity
@Table(name = "plan")
public class Plan {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_idx")
    private int planIdx;

    @ManyToOne
    @JoinColumn(name = "team_idx")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "recent_user_idx")
    private User user;

    private String title;

    private String content;

    private LocalDateTime date;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    private String location;

    private float latitude;

    private float longitude;

    private int cost;
}
