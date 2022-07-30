package com.weave.weaveserver.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name="image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "ImageSequence")
    @Column(unique=true,name = "image_idx")
    private Long imageIdx;

    @Column(nullable = false)
    private String url;

    @JoinColumn(name = "archive_idx", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY) //외래키
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Archive archive;

    public void updateImage(String url) {
        this.url = url;
    }

    public Image(String url) {
        this.url = url;
    }
}
