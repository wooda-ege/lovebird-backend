package com.ege.wooda.domain.member.dto.request;

import com.ege.wooda.domain.member.domain.Gender;
import com.ege.wooda.domain.member.domain.Member;
import lombok.Builder;
import org.apache.commons.codec.binary.StringUtils;

import java.time.LocalDate;
import java.util.List;

public record MemberUpdateRequest(String nickname,
                                  String firstDate,
                                  String gender) {

    @Builder
    public MemberUpdateRequest {}

    public Member toEntity() {
        return Member.builder()
                .nickname(nickname)
                .firstDate(LocalDate.parse(firstDate))
                .gender(StringUtils.equals(gender, "MALE") ? Gender.MALE : Gender.FEMALE)
                .build();

    }
}
