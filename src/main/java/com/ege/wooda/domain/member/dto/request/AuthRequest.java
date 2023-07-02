package com.ege.wooda.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(@NotBlank String authToken) {
}
