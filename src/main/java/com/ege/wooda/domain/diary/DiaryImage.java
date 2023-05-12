package com.ege.wooda.domain.diary;

import com.ege.wooda.global.audit.AuditEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiaryImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="image_id")
    private Long id;

    @Column(name="image_url", nullable = false)
    private String url;

    // 논리적 외래키
    @Column(name="diary_id", nullable = false)
    private Long diaryId;

    @Embedded
    private AuditEntity auditEntity;

    @Builder
    public void DiaryImage(String url, Long diaryId){
        this.url=url;
        this.diaryId=diaryId;
        auditEntity=new AuditEntity();
    }

    public void updateImage(DiaryImage di){
        url=di.getUrl();
        diaryId=di.getDiaryId();
    }
}
