package com.ege.wooda.domain.diary;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class Diary {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="diary_id")
    private Long id;
    @Column(nullable = false)
    private String title;

    @Column(name="sub_title")
    private String subTitle;

    @Column(name="memory_date", nullable = false)
    private Date memoryDate;
    private String place;

    @Column(columnDefinition = "TEXT")
    private String contents;

    @Temporal(TemporalType.DATE)
    @Column(name="create_date", nullable = false)
    private Date createDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="update_date")
    private Date updateDate;

    @Builder
    public Diary(String title, String subTitle, Date memoryDate, String place, String contents, Date createDate, Date updateDate) {
        this.title = title;
        this.subTitle = subTitle;
        this.memoryDate = memoryDate;
        this.place = place;
        this.contents = contents;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }
}
