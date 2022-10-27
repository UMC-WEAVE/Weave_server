package com.weave.weaveserver.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Reason extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "ReasonSequence")
    @Column(unique=true)
    private Long reasonIdx;

    @Column
    private boolean item1;

    @Column
    private boolean item2;

    @Column
    private boolean item3;

    @Column(length = 300)
    private String subItem;
}
