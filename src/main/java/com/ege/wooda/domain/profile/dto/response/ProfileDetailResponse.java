package com.ege.wooda.domain.profile.dto.response;

import java.time.LocalDate;
import java.util.Map;

import com.ege.wooda.domain.profile.domain.enums.Anniversary;

import lombok.Builder;

public record ProfileDetailResponse(Long memberId,
                                    String nickname,
                                    Map<Anniversary, LocalDate> anniversaryList,
                                    String gender,
                                    String profileImageUrl,
                                    boolean linkedFlag) {

    @Builder
    public ProfileDetailResponse {}

}
