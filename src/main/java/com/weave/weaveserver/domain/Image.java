package com.weave.weaveserver.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name="image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "ImageSequence")
    @Column(unique=true)
    private Long imageIdx;

    @Column(nullable = false)
    private String url;

    public void updateImage(String url) {
        this.url = url;
    }
}
