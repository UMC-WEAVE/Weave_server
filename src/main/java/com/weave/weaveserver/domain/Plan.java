package com.weave.weaveserver.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.tomcat.jni.Local;

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
    private int planIdx;
    private int teamIdx;

    @ManyToOne
    @JoinColumn(name = "recent_user_idx")
    private User user;

    private String title;

    private String content;

    private LocalDateTime date;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String location;

    private float latitude;

    private float longtitude;

    private int cost;
}
