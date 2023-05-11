package com.ege.wooda.domain.diary.dao;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "DiaryImage")
@Data
public class DiaryImage {
    @Id
    @GeneratedValue
    @Column(name="image_id")
    private Long id;

    @Column(name="image_url")
    private String url;
    // 논리적 외래키
    @Column(name="diary_id")
    private Long DiaryId;
}
