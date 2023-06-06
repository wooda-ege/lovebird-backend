package com.ege.wooda.domain.diary.controller;

import com.ege.wooda.domain.diary.domain.Diary;
import com.ege.wooda.domain.diary.dto.request.DiaryCreateRequest;
import com.ege.wooda.domain.diary.dto.response.DiaryResponseMessage;
import com.ege.wooda.domain.diary.dto.request.DiaryUpdateRequest;
import com.ege.wooda.domain.member.service.MemberService;
import com.ege.wooda.global.common.response.ApiResponse;
import com.ege.wooda.domain.diary.service.DiaryService;

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
@RequestMapping("/api/v0/diaries")
public class DiaryController {

    private final DiaryService diaryService;
    private final MemberService memberService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<Long>> save(
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @Validated @RequestPart(value = "diaryCreateRequest") DiaryCreateRequest diary) throws IOException {
        String memberUUID = memberService.findById(diary.memberId()).getUuid();
        Long id = diaryService.save(images, diary, memberUUID);
        return new ResponseEntity<>(
                ApiResponse.createSuccessWithData(DiaryResponseMessage.CREATE_DIARY.getMessage(), id),
                HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<Diary>>> getList() {
        List<Diary> diaryList = diaryService.findDiaries();

        return ResponseEntity.ok(
                ApiResponse.createSuccessWithData(DiaryResponseMessage.READ_DIARY.getMessage(), diaryList)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Diary>> getOne(@PathVariable Long id) {
        Diary diary = diaryService.findById(id);

        return ResponseEntity.ok(
                ApiResponse.createSuccessWithData(DiaryResponseMessage.READ_DIARY.getMessage(), diary));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Long>> update(@PathVariable Long id,
                                                    @RequestPart(value = "images", required = false)
                                                    List<MultipartFile> images,
                                                    @Validated @RequestPart(value = "diaryUpdateRequest")
                                                    DiaryUpdateRequest diaryUpdateRequest) throws IOException {
        String memberUUID = memberService.findById(diaryUpdateRequest.memberId()).getUuid();
        Long updatedId = diaryService.update(id, images, diaryUpdateRequest, memberUUID);
        return ResponseEntity.ok(
                ApiResponse.createSuccessWithData(DiaryResponseMessage.UPDATE_DIARY.getMessage(), updatedId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {
        String memberUUID = memberService.findById(diaryService.findById(id).getMemberId()).getUuid();
        diaryService.delete(id, memberUUID);
        return ResponseEntity.ok(ApiResponse.createSuccess(DiaryResponseMessage.DELETE_DIARY.getMessage()));
    }
}