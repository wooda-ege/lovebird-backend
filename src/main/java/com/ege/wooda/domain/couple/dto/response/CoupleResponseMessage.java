package com.ege.wooda.domain.couple.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoupleResponseMessage {
    GENERATE_COUPLE_CODE("커플 연동 코드가 발급되었습니다."),
    SEND_CODE_SUCCESS("커플 연동 코드를 발송하었습니다."),
    SEND_CODE_FAIL("커플 연동 코드 발송에 실패하었습니다."),
    LINK_SUCCESS("커플 연동에 성공하셨습니다"),
    LINK_FAIL("커플 연동에 실패하였습니다");
    private final String message;
}
