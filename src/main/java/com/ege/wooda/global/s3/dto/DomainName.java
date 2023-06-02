package com.ege.wooda.global.s3.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DomainName {
    MEMBER("profile"),
    DIARY("diary");

    private final String domain;
}
