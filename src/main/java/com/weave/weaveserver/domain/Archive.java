package com.weave.weaveserver.domain;

import com.sun.istack.NotNull;
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

    @NotNull
    @Column(length = 500)
    private String title;

    @Column(length = 200)
    private String content;

    @Column(length = 500)
    private String url;

    @Column(name = "image_url")
    private String imageUrl;

    @NotNull
    @Column(name = "is_pinned")
    private boolean isPinned;

    @NotNull
    @JoinColumn(name = "category_idx")
    @ManyToOne(fetch = FetchType.LAZY) //외래키
    private Category categoryIdx;

    @NotNull
    @JoinColumn(name = "user_idx")
    @ManyToOne(fetch = FetchType.LAZY) //외래키
    private User user;

    @NotNull
    @JoinColumn(name = "team_idx")
    @ManyToOne(fetch = FetchType.LAZY) //외래키
    private Team team;
}
