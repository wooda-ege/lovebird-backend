package com.ege.wooda.domain.member.dto.request;

import com.ege.wooda.domain.member.domain.Gender;
import com.ege.wooda.domain.member.domain.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.apache.commons.codec.binary.StringUtils;

import java.time.LocalDate;

public record MemberUpdateRequest(@NotBlank String nickname,
                                  @NotBlank String firstDate,
                                  @NotBlank String gender) {

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
