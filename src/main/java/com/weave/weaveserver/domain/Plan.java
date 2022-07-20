package com.weave.weaveserver.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "PlanSequence")
    @Column(unique=true)
    private Long planIdx;

    @ManyToOne
    @JoinColumn(name = "team_idx")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "recent_user_idx")
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    @Column
    private String location;

    @Column
    private double latitude;

    @Column
    private double longitude;

    @Column(nullable = false)
    private int cost;

    @Column
    private boolean isModified;

    public void updatePlan(User user, String title, LocalDateTime startTime, LocalDateTime endTime, String location, int cost ) {
        this.user = user;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.cost = cost;
    }
}
