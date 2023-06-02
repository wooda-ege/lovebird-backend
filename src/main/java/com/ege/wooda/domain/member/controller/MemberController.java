package com.ege.wooda.domain.member.controller;

import com.ege.wooda.domain.member.dto.request.MemberCreateRequest;
import com.ege.wooda.domain.member.dto.request.MemberUpdateRequest;
import com.ege.wooda.domain.member.dto.response.MemberDetailResponse;
import com.ege.wooda.domain.member.dto.response.MemberResponseMessage;
import com.ege.wooda.domain.member.service.MemberService;
import com.ege.wooda.global.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/members")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<Long>> add(@RequestPart(value = "images") List<MultipartFile> images,
                                                 @Validated @RequestPart(value = "memberCreateRequest")
                                                 MemberCreateRequest memberCreateRequest) throws IOException {
        Long addMemberId = memberService.save(images, memberCreateRequest);
        return new ResponseEntity<>(
                ApiResponse.createSuccessWithData(MemberResponseMessage.CREATE_MEMBER.getMessage(),
                                                  addMemberId),
                HttpStatus.CREATED);
    }

    @GetMapping("/{nickname}")
    public ResponseEntity<ApiResponse<MemberDetailResponse>> details(@PathVariable String nickname) {
        MemberDetailResponse memberDetailResponse = memberService.findMemberByNickname(nickname)
                                                                 .toMemberDetailResponse();
        return ResponseEntity.ok(
                ApiResponse.createSuccessWithData(MemberResponseMessage.READ_MEMBER.getMessage(),
                                                  memberDetailResponse));
    }

    @PutMapping("/{nickname}")
    public ResponseEntity<ApiResponse<Long>> modify(@PathVariable String nickname,
                                                    @RequestPart(value = "images") List<MultipartFile> images,
                                                    @Validated @RequestPart(value = "memberUpdateRequest")
                                                    MemberUpdateRequest memberUpdateRequest)
            throws IOException {
        Long modifyMemberId = memberService.update(nickname, images, memberUpdateRequest);
        return ResponseEntity.ok(
                ApiResponse.createSuccessWithData(MemberResponseMessage.UPDATE_MEMBER.getMessage(),
                                                  modifyMemberId));
    }

    @DeleteMapping("/{nickname}")
    public ResponseEntity<ApiResponse<?>> remove(@PathVariable String nickname) {
        memberService.delete(nickname);
        return ResponseEntity.ok(ApiResponse.createSuccess(MemberResponseMessage.DELETE_MEMBER.getMessage()));
    }
}
