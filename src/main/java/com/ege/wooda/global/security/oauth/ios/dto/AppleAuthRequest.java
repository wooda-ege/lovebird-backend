package com.ege.wooda.global.security.oauth.ios.dto;

import com.ege.wooda.global.security.oauth.ios.dto.user.AppleUser;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record AppleAuthRequest(@NotBlank String id_token,    // CamelCase 로 받을 수 있는지 테스트 후 수정
                               @NotEmpty AppleUser user) {

    public String getName() {
        return user.getFullName();
    }

    public String getEmail() {
        return user.email();
    }
}
