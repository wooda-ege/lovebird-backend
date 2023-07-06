package com.ege.wooda.domain.member.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberResponseMessage {
    CREATE_TOKEN_SUCCESS("토큰이 생성되었습니다."),
    CREATE_TOKEN_FAIL("토큰이 생성이 실패했습니다."),
    FAIL_AUTHORIZATION_APPLE("애플 로그인 검증에 실패했습니다.");

    private final String message;
}
