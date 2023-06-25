package com.ege.wooda.domain.profile.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProfileResponseMessage {
    CREATE_PROFILE("프로필이 생성되었습니다."),
    READ_PROFILE("프로필 조회에 성공하셨습니다."),
    READ_PROFILE_FAIL("프로필 조회에 실패셨습니다."),
    UPDATE_PROFILE("프로필이 성공적으로 수정되었습니다.");
    private final String message;
}
