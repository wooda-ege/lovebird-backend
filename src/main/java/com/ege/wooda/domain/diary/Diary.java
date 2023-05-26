package com.ege.wooda.domain.diary;

import com.ege.wooda.domain.diary.dto.response.DiaryDetailResponse;
import com.ege.wooda.global.audit.AuditEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="diary_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private Long memberId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name="sub_title")
    private String subTitle;

    @Column(name="memory_date", nullable = false)
    private LocalDate memoryDate;
    private String place;

    @Column(name = "contents")
    private String contents;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name="diary_images", joinColumns = @JoinColumn(name = "diary_id"))
    @Column(name="img_urls")
    private List<String> imgUrls=new ArrayList<>();

    @Embedded
    private AuditEntity auditEntity;

    @Builder
    public Diary(Long memberId, String title, String subTitle, LocalDate memoryDate, String place, String contents, List imgUrls) {
        this.memberId=memberId;
        this.title = title;
        this.subTitle = subTitle;
        this.memoryDate = memoryDate;
        this.place = place;
        this.contents = contents;
        this.imgUrls=imgUrls;
        auditEntity=new AuditEntity();
    }

    public void updateDiary(Diary d){
        memberId=d.getMemberId();
        title=d.getTitle();
        subTitle=d.getSubTitle();
        memoryDate=d.getMemoryDate();
        place=d.getPlace();
        contents=d.getContents();
        imgUrls=d.getImgUrls();
    }

    public DiaryDetailResponse toDiaryDetailResponse(){
        return DiaryDetailResponse.builder()
                .memberId(memberId)
                .title(title)
                .subTitle(subTitle)
                .memoryDate(memoryDate)
                .place(place)
                .contents(contents)
                .imgUrls(imgUrls)
                .build();
    }
}
