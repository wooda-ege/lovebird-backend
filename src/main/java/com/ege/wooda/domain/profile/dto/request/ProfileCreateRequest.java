package com.ege.wooda.domain.profile.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProfileCreateRequest(@NotBlank String nickname,
                                  @NotBlank String firstDate,
                                  @NotBlank String gender) {
}
