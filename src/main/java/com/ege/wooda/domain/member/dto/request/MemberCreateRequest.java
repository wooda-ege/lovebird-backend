package com.ege.wooda.domain.member.dto.request;

import com.ege.wooda.domain.member.domain.Gender;
import com.ege.wooda.domain.member.domain.Member;
import lombok.Builder;
import org.apache.commons.codec.binary.StringUtils;

import java.time.LocalDate;
import java.util.List;

public record MemberCreateRequest(String nickname,
                                  String firstDate,
                                  String gender) {
    @Builder
    public MemberCreateRequest {}

    public Member toEntity(List<String> imageUrls) {
        return Member.builder()
                .nickname(nickname)
                .firstDate(LocalDate.parse(firstDate))
                .gender(StringUtils.equals(gender, "MALE") ? Gender.MALE : Gender.FEMALE)
                .pictureM(imageUrls.get(0))
                .pictureW(imageUrls.get(1))
                .build();
    }
}
