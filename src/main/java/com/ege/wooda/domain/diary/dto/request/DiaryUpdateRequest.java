package com.ege.wooda.domain.diary.dto.request;

import com.ege.wooda.domain.diary.domain.Diary;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public record DiaryUpdateRequest(@NotBlank String title,
                                 @NotBlank String subTitle,
                                 @NotBlank String memoryDate,
                                 @NotBlank String place,
                                 @NotBlank String content) {

    @Builder
    public DiaryUpdateRequest {}

    public Diary toEntity(Long memberId, List<String> imgUrls) {
        return Diary.builder()
                    .memberId(memberId)
                    .title(title)
                    .subTitle(subTitle)
                    .memoryDate(LocalDate.parse(memoryDate))
                    .place(place)
                    .content(content)
                    .imgUrls(imgUrls)
                    .build();
    }
}
