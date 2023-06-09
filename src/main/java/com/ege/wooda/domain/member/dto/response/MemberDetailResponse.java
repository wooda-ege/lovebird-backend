package com.ege.wooda.domain.member.dto.response;

import java.util.Map;

import lombok.Builder;

public record MemberDetailResponse(String uuid,
                                   String nickname,
                                   Map<String, Object> anniversaryList,
                                   String gender,
                                   String pictureM,
                                   String pictureW) {

    @Builder
    public MemberDetailResponse {}

}
