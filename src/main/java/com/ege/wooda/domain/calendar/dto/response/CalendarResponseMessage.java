package com.ege.wooda.domain.calendar.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CalendarResponseMessage {
    CREATE_CALENDAR("일정이 등록되었습니다."),
    READ_CALENDAR("일정 조회에 성공하셨습니다."),
    UPDATE_CALENDAR("일정 정보가 수정되었습니다."),
    DELETE_CALENDAR("일정 정보가 삭제되었습니다."),
    BAD_REQUEST_CALENDAR("요청 정보가 잘못되었습니다."),
    FAIL_READ_CALENDAR("해당 일정이 존재하지 않습니다.");

    private final String message;
}
