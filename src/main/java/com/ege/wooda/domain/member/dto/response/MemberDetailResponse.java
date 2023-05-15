package com.ege.wooda.domain.member.dto.response;

import lombok.Builder;

import java.time.LocalDate;

public record MemberDetailResponse(String nickname,
                                   long dDay,
                                   String gender,
                                   String pictureM,
                                   String pictureW) {

    @Builder
    public MemberDetailResponse {}


}