package com.ege.wooda.domain.diary.controller;

import com.ege.wooda.domain.diary.Diary;
import com.ege.wooda.domain.diary.dto.request.DiaryCreateRequest;
import com.ege.wooda.domain.diary.dto.response.DiaryDetailResponse;
import com.ege.wooda.domain.diary.dto.response.DiaryResponseMessage;
import com.ege.wooda.domain.diary.dto.request.DiaryUpdateRequest;
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

    @PostMapping("")
    public ResponseEntity<ApiResponse<Long>> save(
            @RequestPart(value = "images") List<MultipartFile> imgs,
            @Validated @RequestPart(value = "diaryCreateRequest") DiaryCreateRequest diary) throws IOException {
        Long id = diaryService.save(imgs, diary);
        return new ResponseEntity<>(
                ApiResponse.createSuccessWithData(DiaryResponseMessage.CREATE_DIARY.getMessage(), id),
                HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<List>> getList() {
        List<Diary> diaryList = diaryService.findDiaries();

        return ResponseEntity.ok(
                ApiResponse.createSuccessWithData(DiaryResponseMessage.READ_DIARY.getMessage(), diaryList)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DiaryDetailResponse>> getOne(@PathVariable Long id) {
        DiaryDetailResponse diaryDetailResponse = diaryService.findOne(id).toDiaryDetailResponse();

        return ResponseEntity.ok(ApiResponse.createSuccessWithData(DiaryResponseMessage.READ_DIARY.getMessage(), diaryDetailResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Long>> update(@PathVariable Long id,
                                                    @RequestPart(value = "imgs") List<MultipartFile> imgs,
                                                    @Validated @RequestPart(value = "diaryUpdateRequest") DiaryUpdateRequest diaryUpdateRequest) throws IOException {

        Long updatedId = diaryService.update(id, imgs, diaryUpdateRequest);
        return ResponseEntity.ok(ApiResponse.createSuccessWithData(DiaryResponseMessage.UPDATE_DIARY.getMessage(), id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {
        diaryService.delete(id);
        return ResponseEntity.ok(ApiResponse.createSuccess(DiaryResponseMessage.DELETE_DIARY.getMessage()));
    }
}