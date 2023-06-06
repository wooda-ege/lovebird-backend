package com.ege.wooda.domain.diary.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public record DiaryDetailResponse(Long memberId,
                                  String title,
                                  String subTitle,
                                  LocalDate memoryDate,
                                  String place,
                                  String content,
                                  List<String> imgUrls) {

    @Builder
    public DiaryDetailResponse {}
}
