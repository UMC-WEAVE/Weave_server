package com.weave.weaveserver.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicUpdate
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

    @Column
    private Long imgUploadTime;

    private boolean isEmpty; //boolean 은 bit(1)로 저장, 1(true)는 속한 팀원이 없음 / 0(false)는 속한 팀원이 있음

    public void uploadImage(String imgUrl, Long imgUploadTime) {
        this.imgUrl = imgUrl;
        this.imgUploadTime = imgUploadTime;
    }
    public void updateEmpty(){
        this.isEmpty = false;
    }

    public void updateTeam(String title, LocalDate startDate, LocalDate endDate){
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void changeLeader(User user){
        this.leader = user;
    }

    //TODO : 이부분 바꿈!!
    public void setLeader(User leader) {
        this.leader = leader;
    }
}
