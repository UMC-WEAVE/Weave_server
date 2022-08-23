package com.weave.weaveserver.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.bytebuddy.asm.Advice;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    @JoinColumn(nullable = false, name = "team_idx")
    private Team team;

    @ManyToOne
    @JoinColumn(nullable = false, name = "recent_user_idx")
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column
    private LocalTime endTime;

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

    public void updatePlan(User user, String title, LocalDate date, LocalTime startTime, LocalTime endTime, String location, double latitude, double longitude, int cost ) {
        this.isModified = true;
        this.user = user;
        this.title = title;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        if(!this.location.equals(location)){
            updateLocation(location, latitude, longitude);
        }
        this.cost = cost;
    }

    public void updateLocation(String location, double latitude, double longitude){
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String dayOfDate(LocalDate date){
        String[] days = {"일", "월", "화", "수", "목", "금", "토"};

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int dayNum = dayOfWeek.getValue();

        return days[dayNum];
    }

    //TODO : 이부분 바꿈!!(휘영)
    public void setUser(User user) {
        this.user = user;
    }
}
