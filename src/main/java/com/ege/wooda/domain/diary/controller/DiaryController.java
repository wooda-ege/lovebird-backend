package com.ege.wooda.domain.diary.controller;

import com.ege.wooda.domain.diary.Diary;
import com.ege.wooda.domain.diary.dto.DiaryCreateRequest;
import com.ege.wooda.domain.diary.dto.DiaryUpdateRequest;
import com.ege.wooda.global.common.response.DefaultResponse;
import com.ege.wooda.global.common.response.ResponseMessage;
import com.ege.wooda.global.common.response.StatusCode;
import com.ege.wooda.domain.diary.service.DiaryService;
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
    public ResponseEntity save(@RequestBody DiaryCreateRequest diary){
        try{
            System.out.println(diary);
            Long id = diaryService.save(diary);
            System.out.printf(diary.title());
        }catch (Exception e){
            return new ResponseEntity(DefaultResponse.response(StatusCode.BAD_REQUEST, ResponseMessage.FAILED_CREATE_DIARY), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(DefaultResponse.response(StatusCode.OK, ResponseMessage.CREATED_DIARY), HttpStatus.OK);
    }

    @GetMapping("/diaries")
    public ResponseEntity<Map<String,Object>> getList(){
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
    public ResponseEntity<Map<String,Object>> getOne(@PathVariable Long id){
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

    @PutMapping("/diaries/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody DiaryUpdateRequest diaryUpdateRequest){
        Map<String, Object> result=new HashMap<>();
        try{
            Long updatedId=diaryService.update(id, diaryUpdateRequest);
            result.put("statusCode", StatusCode.OK);
            result.put("responseMessage", ResponseMessage.READ_DIARY);
            result.put("data", diaryService.findOne(updatedId));
        }catch (Exception e){
            return new ResponseEntity(DefaultResponse.response(StatusCode.BAD_REQUEST, ResponseMessage.DIARY_NOT_FOUND),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Map<String,Object>>(result,HttpStatus.OK);
    }

    @DeleteMapping("/diaries/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id){
        Map<String, Object> result=new HashMap<>();
        try{
            diaryService.delete(id);
            result.put("statusCode", StatusCode.OK);
            result.put("responseMessage", ResponseMessage.READ_DIARY);
            diaryService.findOne(id);
        }
        catch (IllegalArgumentException ie){
            return new ResponseEntity(DefaultResponse.response(StatusCode.OK, ResponseMessage.READ_DIARY),HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity(DefaultResponse.response(StatusCode.BAD_REQUEST, ResponseMessage.DIARY_NOT_FOUND),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Map<String,Object>>(result,HttpStatus.OK);
    }

    @PostMapping("/image")
    public ResponseEntity<Map<String,Object>> postImage(@RequestBody List<MultipartFile> img) throws IOException {
        Map<String, Object> result=new HashMap<>();
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
