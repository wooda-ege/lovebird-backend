package com.ege.wooda.domain.calendar.domain;

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
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Calendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="calendar_id")
    private Long id;

    @Column(name="user_id")
    private Long memberId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name="start_date", nullable = false)
    private LocalDateTime startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name="end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="memo")
    private String memo;

    @Embedded
    private AuditEntity auditEntity;

    @Builder
    public Calendar(Long memberId, String title, String memo, LocalDateTime startDate, LocalDateTime endDate){
        this.memberId=memberId;
        this.title=title;
        this.memo=memo;
        this.startDate=startDate;
        this.endDate=endDate;
        auditEntity=new AuditEntity();
    }

    public void updateCalendar(Calendar c){
        memberId=c.getMemberId();
        title=c.getTitle();
        memo=c.getMemo();
        startDate=c.getStartDate();
        endDate=c.getEndDate();
    }

}
