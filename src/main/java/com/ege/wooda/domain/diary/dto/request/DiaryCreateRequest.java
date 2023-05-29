package com.ege.wooda.domain.diary.dto.request;

import com.ege.wooda.domain.diary.Diary;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public record DiaryCreateRequest(Long memberId,String title, String sub_title, LocalDate memory_date, String place, String contents, List<String> imgUrls){

    @Builder
    public DiaryCreateRequest {}

    public Diary toEntity(List<String> imgUrls){
        return Diary.builder()
                .memberId(memberId)
                .title(title)
                .subTitle(sub_title)
                .memoryDate(memory_date)
                .place(place)
                .contents(contents)
                .imgUrls(imgUrls)
                .build();
    }
}
