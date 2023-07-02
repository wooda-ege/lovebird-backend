package com.ege.wooda.domain.diary.controller;

import com.ege.wooda.domain.diary.domain.Diary;
import com.ege.wooda.domain.diary.dto.request.DiaryCreateRequest;
import com.ege.wooda.domain.diary.dto.request.DiaryUpdateRequest;
import com.ege.wooda.domain.diary.dto.response.DiaryResponseMessage;
import com.ege.wooda.global.common.response.ApiResponse;
import com.ege.wooda.domain.diary.service.DiaryService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/{id}/diaries")
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping("")
    @PreAuthorize("#memberId == authentication.principal.id")
    public ResponseEntity<ApiResponse<Long>> save(@PathVariable("id") Long memberId,
                                                  @RequestPart(value = "images", required = false)
                                                  List<MultipartFile> images,
                                                  @Validated @RequestPart(value = "diaryCreateRequest")
                                                  DiaryCreateRequest diary) throws IOException {
        Long id = diaryService.save(images, diary, memberId);
        return new ResponseEntity<>(
                ApiResponse.createSuccessWithData(DiaryResponseMessage.CREATE_DIARY.getMessage(), id),
                HttpStatus.CREATED);
    }

    @GetMapping("")
    @PreAuthorize("#memberId == authentication.principal.id")
    public ResponseEntity<ApiResponse<List<Diary>>> getDiary(@PathVariable("id") Long memberId) {
        List<Diary> diaryList = diaryService.findByMemberId(memberId);

        return ResponseEntity.ok(
                ApiResponse.createSuccessWithData(DiaryResponseMessage.READ_DIARY.getMessage(), diaryList)
        );
    }

    @GetMapping("/{diaryId}")
    @PreAuthorize("#memberId == authentication.principal.id")
    public ResponseEntity<ApiResponse<Diary>> getOne(@PathVariable("id") Long memberId,
                                                     @PathVariable("diaryId") Long diaryId) {
        Diary diary = diaryService.findByMemberIdAndDiaryId(memberId, diaryId);

        return ResponseEntity.ok(
                ApiResponse.createSuccessWithData(DiaryResponseMessage.READ_DIARY.getMessage(), diary));
    }

    @PutMapping("/{diaryId}")
    @PreAuthorize("#memberId == authentication.principal.id")
    public ResponseEntity<ApiResponse<Long>> update(@PathVariable("id") Long memberId,
                                                    @PathVariable("diaryId") Long diaryId,
                                                    @RequestPart(value = "images", required = false)
                                                    List<MultipartFile> images,
                                                    @Validated @RequestPart(value = "diaryUpdateRequest")
                                                    DiaryUpdateRequest diaryUpdateRequest) throws IOException {
        Long updatedId = diaryService.update(diaryId, images, diaryUpdateRequest, memberId);
        return ResponseEntity.ok(
                ApiResponse.createSuccessWithData(DiaryResponseMessage.UPDATE_DIARY.getMessage(), updatedId));
    }

    @DeleteMapping("/{diaryId}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable("id") Long memberId,
                                                 @PathVariable("diaryId") Long diaryId) {
        diaryService.delete(memberId, diaryId);
        return ResponseEntity.ok(ApiResponse.createSuccess(DiaryResponseMessage.DELETE_DIARY.getMessage()));
    }
}