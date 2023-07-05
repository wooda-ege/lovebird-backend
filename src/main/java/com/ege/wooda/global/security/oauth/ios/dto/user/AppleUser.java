package com.ege.wooda.global.security.oauth.ios.dto.user;

public record AppleUser(String email,
                        Name name) {

    public String getFullName() {
        return name.lastName() + name.firstName();
    }
}
