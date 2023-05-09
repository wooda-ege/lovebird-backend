package com.ege.wooda.domain.member;

import com.ege.wooda.global.audit.AuditEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(name = "first_date", nullable = false)
        private LocalDate firstDate;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    @Column(name = "picture_m")
    private String pictureM;

    @Column(name = "picture_w")
    private String pictureW;

    @Embedded
    private AuditEntity auditEntity;

    @Builder
    public Member(String nickname, LocalDate firstDate, Gender gender, String pictureM, String pictureW) {
        this.nickname = nickname;
        this.firstDate = firstDate;
        this.gender = gender;
        this.pictureM = pictureM;
        this.pictureW = pictureW;
        auditEntity = new AuditEntity();
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfile(Member user) {
        firstDate = user.getFirstDate();
        pictureM = user.getPictureM();
        pictureW = user.getPictureW();
        gender = user.getGender();
    }
}
