package com.ege.wooda.global.security.oauth.model;

import com.ege.wooda.domain.member.domain.Oauth2Entity;
import com.ege.wooda.global.security.oauth.model.enums.SocialProvider;

import lombok.Builder;

public record OAuth2Request(String accountId, String name, String email, SocialProvider socialProvider) {

    @Builder
    public OAuth2Request {}

    public Oauth2Entity toOauth2Entity() {
        return new Oauth2Entity(socialProvider, accountId, name, email);
    }
}

