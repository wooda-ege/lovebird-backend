package com.ege.wooda.domain.calendar.domain;

import com.ege.wooda.domain.calendar.dto.response.CalendarDetailResponse;
import com.ege.wooda.global.audit.AuditEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Calendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="calendar_id")
    private Long id;

    @Column(name="member_id")
    private Long memberId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name="start_date", nullable = false)
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name="end_date")
    private LocalDate endDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    @DateTimeFormat(pattern = "HH:mm")
    @Column(name="startTime")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    @DateTimeFormat(pattern = "HH:mm")
    @Column(name="endTime")
    private LocalTime endTime;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="memo")
    private String memo;

    @Embedded
    private AuditEntity auditEntity;

    @Builder
    public Calendar(Long memberId, String title, String memo, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime){
        this.memberId=memberId;
        this.title=title;
        this.memo=memo;
        this.startDate=startDate;
        this.endDate=endDate;
        this.startTime=startTime;
        this.endTime=endTime;
        auditEntity=new AuditEntity();
    }

    public void updateCalendar(Calendar c){
        memberId=c.getMemberId();
        title=c.getTitle();
        memo=c.getMemo();
        startDate=c.getStartDate();
        endDate=c.getEndDate();
        startTime=c.getStartTime();
        endTime=c.getEndTime();
    }

    public CalendarDetailResponse toCalendarDetailResponse(){

        return CalendarDetailResponse.builder()
                .id(id)
                .memberId(memberId)
                .title(title)
                .memo(memo)
                .startDate(startDate)
                .endDate(endDate)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

}
