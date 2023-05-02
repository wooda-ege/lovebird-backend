package com.ege.wooda.global.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

/**
 * @author          locmment
 * @record          BaseTime
 * @description     JPA Auditing 기능 관련 클래스
 */

@Getter
@Embeddable
public class BaseTime {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}