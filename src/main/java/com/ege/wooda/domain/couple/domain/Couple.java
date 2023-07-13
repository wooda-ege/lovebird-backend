package com.ege.wooda.domain.couple.domain;

import com.ege.wooda.domain.couple.dto.response.CoupleCodeResponse;
import com.ege.wooda.global.audit.AuditEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Couple {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "couple_code")
    private String coupleCode;

    @Column(name = "use_code", nullable = false)
    private Boolean useCode;

    @Embedded
    private AuditEntity auditEntity;

    @Builder
    public Couple(String coupleCode, Long memberId){
        this.memberId=memberId;
        this.coupleCode=coupleCode;
        this.useCode=Boolean.FALSE;
        this.auditEntity=new AuditEntity();
    }

    public void updateCoupleCode(String coupleCode) {
        this.coupleCode = coupleCode;
    }

    public void deleteCoupleCode() {
        this.coupleCode = null;
    }

    public void connectCouple(String coupleCode) {
        this.coupleCode = coupleCode;
    }

    public void expireCode() { this.useCode = Boolean.TRUE; }

    public Boolean isExpired(){
        if(ChronoUnit.HOURS.between(this.auditEntity.getCreatedAt(),LocalDateTime.now())>=24 ) return Boolean.TRUE;
        return Boolean.FALSE;
    }

    public CoupleCodeResponse toCoupleCodeResponse() {
        return new CoupleCodeResponse(memberId, coupleCode);
    }
}
