package com.weave.weaveserver.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamIdx;

    @ManyToOne
    @JoinColumn(name = "leader_idx")
    private User leader;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column
    private String imgUrl;

    private boolean isEmpty; //boolean 은 bit(1)로 저장, 1(true)는 속한 팀원이 없음 / 0(false)는 속한 팀원이 있음

}
