package com.weave.weaveserver.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@DynamicUpdate //?
@Table(name = "Archive")
public class Archive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "archive_idx")
    private int archiveIdx;

    @JoinColumn(name = "user_idx")
    private int userIdx;

    //    @JoinColumn(name = "team_idx") //지금은 아직 없으니까 일단 일반 컬럼으로..
    @Column(name = "team_idx")
    private int teamIdx;

    private String title;

    private String content;

    private String url;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_pinned")
    private boolean isPinned;

//    @JoinColumn(name = "category_idx") //지금은 아직 없으니까 일단 일반 컬럼으로..
    @Column(name = "category_idx")
    private int categoryIdx;

}
