package com.ege.wooda.domain.profile.dto.param;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.web.multipart.MultipartFile;

import com.ege.wooda.domain.member.domain.enums.Gender;
import com.ege.wooda.domain.profile.domain.Profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public record ProfileCreateParam(@NotBlank Long memberId,
                                 MultipartFile image,
                                 @NotBlank String nickname,
                                 @NotBlank String firstDate,
                                 @NotBlank Gender gender) {

    @Builder
    public ProfileCreateParam {}

    public Profile toEntity() {
        return Profile.builder()
                      .memberId(memberId)
                      .nickname(nickname)
                      .firstDate(getLocalDate(firstDate))
                      .gender(gender)
                      .build();
    }

    private LocalDate getLocalDate(String memoryDate) {
        return LocalDate.parse(memoryDate, DateTimeFormatter.ISO_DATE);
    }
}
