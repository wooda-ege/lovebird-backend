package com.ege.wooda.controller;

import com.ege.wooda.domain.diary.Diary;
import com.ege.wooda.dto.Diary.DiaryDTO;
import com.ege.wooda.dto.response.DefaultResponse;
import com.ege.wooda.dto.response.ResponseMessage;
import com.ege.wooda.dto.response.StatusCode;
import com.ege.wooda.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping("/diaries")
    public ResponseEntity saveDiary(@RequestBody DiaryDTO diaryDTO){
        try{
            Long id = diaryService.saveDiary(diaryDTO);
        }catch (Exception e){
            return new ResponseEntity(DefaultResponse.response(StatusCode.BAD_REQUEST, ResponseMessage.FAILED_CREATE_DIARY), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(DefaultResponse.response(StatusCode.OK, ResponseMessage.CREATED_DIARY), HttpStatus.OK);
    }

    @GetMapping("/diaries")
    public ResponseEntity<Map<String,Object>> getDiaryList(){
        Map<String, Object> result=new HashMap<>();
        try{
            List<Diary> diaryList=diaryService.findDiaries();
            result.put("statusCode", StatusCode.OK);
            result.put("responseMessage", ResponseMessage.READ_DIARY);
            result.put("data", diaryList);

        }catch (Exception e){
            return new ResponseEntity(DefaultResponse.response(StatusCode.BAD_REQUEST, ResponseMessage.DIARY_NOT_FOUND),HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Map<String,Object>>(result,HttpStatus.OK);
    }

    @GetMapping("/diaries/{id}")
    public ResponseEntity<Map<String,Object>> getDiary(@PathVariable Long id){
        Map<String, Object> result=new HashMap<>();
        try{
            Optional<Diary> diary=diaryService.findOne(id);
            result.put("statusCode", StatusCode.OK);
            result.put("responseMessage", ResponseMessage.READ_DIARY);
            result.put("data", diary);
        }catch (Exception e){
            return new ResponseEntity(DefaultResponse.response(StatusCode.BAD_REQUEST, ResponseMessage.DIARY_NOT_FOUND),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Map<String,Object>>(result,HttpStatus.OK);
    }

    @PostMapping("/image")
    public ResponseEntity<Map<String,Object>> postDiaryImage(@RequestParam("img") List<MultipartFile> img) throws IOException {
        Map<String, Object> result=new HashMap<>();
        for(MultipartFile i : img){
            System.out.println(i);
        }
        try{
            List<String> urlList=diaryService.saveImage(img);
            result.put("statusCode", StatusCode.OK);
            result.put("responseMessage", ResponseMessage.CREATED_DIARY);
            result.put("data", urlList);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(DefaultResponse.response(StatusCode.BAD_REQUEST,ResponseMessage.FAILED_CREATE_DIARY),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Map<String,Object>>(result,HttpStatus.OK);
    }
}
