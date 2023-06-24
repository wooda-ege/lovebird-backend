package com.ege.wooda.domain.member.domain;

import com.ege.wooda.global.security.oauth.model.enums.SocialProvider;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Oauth2Entity {
    @Enumerated(EnumType.STRING)
    @Column(name = "social_provider")
    private SocialProvider socialProvider;

    @Column(name = "account_id", nullable = false, unique = true)
    private String accountId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email")
    private String email;

    @Builder
    public Oauth2Entity(SocialProvider socialProvider, String accountId, String username, String email) {
        this.socialProvider = socialProvider;
        this.accountId = accountId;
        this.username = username;
        this.email = email;
    }
}
