package com.ege.wooda.domain.calendar.domain;

import com.ege.wooda.global.audit.AuditEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CalendarEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name="partner_id")
    private Long partnerId;

    @Column(name = "send_flag")
    private Boolean sendFlag;

    @Column(name = "event_at")
    private LocalDateTime evenAt;

    @Column(name="result")
    private String result;

    @Embedded
    private AuditEntity auditEntity;

    @Builder
    public CalendarEvent(Long memberId, Long partnerId, LocalDateTime evenAt, String result){
        this.memberId=memberId;
        this.partnerId=partnerId;
        this.sendFlag=Boolean.FALSE;
        this.evenAt=evenAt;
        this.result=result;
        this.auditEntity=new AuditEntity();
    }

    public void updateCalenderEvent(CalendarEvent ce){
        memberId=ce.memberId;
        partnerId=ce.partnerId;
        sendFlag=ce.sendFlag;
        evenAt=ce.evenAt;
        result=ce.result;
    }

}
