package com.ege.wooda.global.security.oauth.ios.dto.user;

import jakarta.validation.constraints.NotEmpty;

public record AppleUser(@NotEmpty String email,
                        @NotEmpty Name name) {
}
