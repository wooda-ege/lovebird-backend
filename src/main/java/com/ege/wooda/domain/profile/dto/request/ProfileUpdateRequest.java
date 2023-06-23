package com.ege.wooda.domain.profile.dto.request;

import com.ege.wooda.domain.member.domain.enums.Gender;

import jakarta.validation.constraints.NotBlank;

public record ProfileUpdateRequest(@NotBlank String nickname,
                                   @NotBlank String firstDate,
                                   @NotBlank Gender gender) {
}
