package com.weave.weaveserver.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Entity
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

    public void updatePlan(User user, String title, LocalDateTime startTime, LocalDateTime endTime, String location, int cost ) {
        this.user = user;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.cost = cost;
    }
}
