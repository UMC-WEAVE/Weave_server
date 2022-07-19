package com.weave.weaveserver.domain;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@DynamicUpdate //?
@Table(name = "archive")
public class Archive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "archive_idx")
    private int archiveIdx;

    private String title;

    private String content;

    private String url;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_pinned")
    private boolean isPinned;

//    @JoinColumn(name = "category_idx") //지금은 아직 없으니까 일단 일반 컬럼으로..
//    @ManyToOne //외래키
    @Column(name = "category_idx")
    private int categoryIdx;

    @JoinColumn(name = "user_idx")
    @ManyToOne //외래키
    private User user;

    @JoinColumn(name = "team_idx") //지금은 아직 없으니까 일단 일반 컬럼으로..
//    @Column(name = "team_idx")
    @ManyToOne //외래키
    private Team team;
}
