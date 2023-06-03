package com.ege.wooda.domain.member.dto.response;

import lombok.Builder;

public record MemberDetailResponse(String uuid,
                                   String nickname,
                                   long dDay,
                                   String gender,
                                   String pictureM,
                                   String pictureW) {

    @Builder
    public MemberDetailResponse {}

}
