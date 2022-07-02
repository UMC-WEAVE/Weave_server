package com.weave.weaveserver.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Table(name = "plan")
public class Plan {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_idx")
    private int planIdx;

    @Column(name = "team_idx")
    private int teamIdx;

    @ManyToOne
    @JoinColumn(name = "recent_user_idx")
    private User user;

    private String title;

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
