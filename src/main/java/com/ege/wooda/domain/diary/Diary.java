package com.ege.wooda.domain.diary;

import com.ege.wooda.global.audit.AuditEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="diary_id")
    private Long id;
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name="sub_title")
    private String subTitle;

    @Column(name="memory_date", nullable = false)
    private LocalDate memoryDate;
    private String place;

    @Column(name = "contents")
    private String contents;

    @Embedded
    private AuditEntity auditEntity;

    @Builder
    public Diary(String title, String subTitle, LocalDate memoryDate, String place, String contents) {
        this.title = title;
        this.subTitle = subTitle;
        this.memoryDate = memoryDate;
        this.place = place;
        this.contents = contents;
        auditEntity=new AuditEntity();
    }

    public void updateDiary(Diary d){
        title=d.getTitle();
        subTitle=d.getSubTitle();
        memoryDate=d.getMemoryDate();
        place=d.getPlace();
        contents=d.getContents();
    }
}
