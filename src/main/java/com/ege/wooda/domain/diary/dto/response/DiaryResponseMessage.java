package com.ege.wooda.domain.diary.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DiaryResponseMessage {
    CREATE_DIARY("다이어리가 등록되었습니다."),
    READ_DIARY("다이어리 조회에 성공하셨습니다."),
    UPDATE_DIARY("다이어리 정보가 수정되었습니다."),
    DELETE_DIARY("다이어리 정보가 삭제되었습니다."),
    BAD_REQUEST_DIARY("입력 데이터가 잘못되었습니다."),
    FAIL_READ_DIARY("해당 다이어리가 존재하지 않습니다");

    private final String message;
}
