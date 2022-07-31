package com.weave.weaveserver.domain;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "archive")
public class Archive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "archive_idx")
    private Long archiveIdx;

    //@NotNull //?왜 DB에 notnull 적용이 안되는가...
    @Column(length = 500, nullable = false)
    private String title;

    @Column(length = 200)
    private String content;

    @Column(length = 500)
    private String url;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_pinned", nullable = false)
    private boolean isPinned;

    @JoinColumn(name = "category_idx", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY) //외래키
    private Category category;

    @JoinColumn(name = "user_idx", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY) //외래키
    private User user;

    @JoinColumn(name = "team_idx", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY) //외래키
    private Team team;


    public void activatePin() {
        this.isPinned = true;

    }

    public void updateArchive(boolean isPinned) {
        this.isPinned = isPinned;

    }
}
