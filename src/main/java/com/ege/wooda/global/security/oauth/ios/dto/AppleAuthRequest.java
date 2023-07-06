package com.ege.wooda.global.security.oauth.ios.dto;

import com.ege.wooda.global.security.oauth.ios.dto.user.AppleUser;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record AppleAuthRequest(String identityToken,
                               AppleUser user) {
}
