package com.ege.wooda.global.security.oauth.ios.dto.user;

import jakarta.validation.constraints.NotEmpty;

public record Name(@NotEmpty String firstName,
                   @NotEmpty String lastName) {
}
