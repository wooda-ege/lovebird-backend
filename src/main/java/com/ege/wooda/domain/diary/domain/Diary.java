package com.ege.wooda.domain.diary.domain;

import com.ege.wooda.domain.diary.dto.response.DiaryDetailResponse;
import com.ege.wooda.global.audit.AuditEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long id;

    @Column(name = "user_id")
    private Long memberId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "sub_title")
    private String subTitle;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "memory_date", nullable = false)
    private LocalDate memoryDate;

    @Column(name = "place")
    private String place;

    @Column(name = "content")
    private String content;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "diary_images", joinColumns = @JoinColumn(name = "diary_id"))
    @Column(name = "img_urls")
    private List<String> imgUrls = new ArrayList<>();

    @Embedded
    private AuditEntity auditEntity;

    @Builder
    public Diary(Long memberId, String title, String subTitle, LocalDate memoryDate, String place,
                 String content, List imgUrls) {
        this.memberId = memberId;
        this.title = title;
        this.subTitle = subTitle;
        this.memoryDate = memoryDate;
        this.place = place;
        this.content = content;
        this.imgUrls = imgUrls;
        auditEntity = new AuditEntity();
    }

    public void saveImages(List<String> imagesUrls) {
        this.imgUrls = imagesUrls;
    }

    public void updateDiary(Diary d) {
        memberId = d.getMemberId();
        title = d.getTitle();
        subTitle = d.getSubTitle();
        memoryDate = d.getMemoryDate();
        place = d.getPlace();
        content = d.getContent();
        imgUrls = d.getImgUrls();
    }

    public DiaryDetailResponse toDiaryDetailResponse() {
        return DiaryDetailResponse.builder()
                                  .memberId(memberId)
                                  .title(title)
                                  .subTitle(subTitle)
                                  .memoryDate(memoryDate)
                                  .place(place)
                                  .content(content)
                                  .imgUrls(imgUrls)
                                  .build();
    }
}
