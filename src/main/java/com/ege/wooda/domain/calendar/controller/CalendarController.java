package com.ege.wooda.domain.calendar.controller;

import com.ege.wooda.domain.calendar.domain.Calendar;
import com.ege.wooda.domain.calendar.dto.request.CalendarCreateRequest;
import com.ege.wooda.domain.calendar.dto.request.CalendarUpdateRequest;
import com.ege.wooda.domain.calendar.dto.response.CalendarResponseMessage;
import com.ege.wooda.domain.calendar.service.CalendarService;
import com.ege.wooda.domain.member.service.MemberService;
import com.ege.wooda.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/calendar")
public class CalendarController {
    private final CalendarService calendarService;
    private final MemberService memberService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<Long>> save(
            @Validated @RequestPart(value = "calendarCreateRequest")CalendarCreateRequest calendar
            ) throws IOException {
        Long id = calendarService.save(calendar);
        return new ResponseEntity<>(
                ApiResponse.createSuccessWithData(CalendarResponseMessage.CREATE_CALENDAR.getMessage(), id),
                HttpStatus.CREATED
        );
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<Calendar>>> getCalendar(@RequestParam Long memberId){
        List<Calendar> calendarList=calendarService.findByMemberId(memberId);
        return ResponseEntity.ok(
                ApiResponse.createSuccessWithData(CalendarResponseMessage.READ_CALENDAR.getMessage(),
                        calendarList)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Long>> update(
            @PathVariable Long id,
            @Validated @RequestPart(value = "calendarUpdateRequest") CalendarUpdateRequest calendarUpdateRequest
    ) throws IOException{
        Long updatedId=calendarService.update(id, calendarUpdateRequest);
        return ResponseEntity.ok(
          ApiResponse.createSuccessWithData(CalendarResponseMessage.UPDATE_CALENDAR.getMessage(), updatedId)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.createSuccess(CalendarResponseMessage.DELETE_CALENDAR.getMessage()));
    }
}
