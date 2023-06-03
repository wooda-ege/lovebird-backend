package com.ege.wooda.domain.member.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberResponseMessage {
    CREATE_MEMBER("유저가 등록되었습니다."),
    READ_MEMBER("유저 조회에 성공하셨습니다."),
    UPDATE_MEMBER("유저 정보가 수정되었습니다."),
    DELETE_MEMBER("유저 정보가 삭제되었습니다."),
    BAD_REQUEST_MEMBER("입력 데이터가 잘못되었습니다."),
    FAIL_READ_MEMBER("해당 유저가 존재하지 않습니다"),
    DUPLICATED_NICKNAME("중복된 닉네임으로 변경할 수 없습니다.");;

    private final String message;
}
