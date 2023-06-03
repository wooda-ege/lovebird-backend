package com.ege.wooda.domain.member.domain;

import com.ege.wooda.domain.member.dto.response.MemberDetailResponse;
import com.ege.wooda.global.audit.AuditEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.DAYS;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "uuid", unique = true, nullable = false)
    private String uuid;

    @Column(name = "nickname", unique = true, nullable = false)
    private String nickname;

    @Column(name = "first_date", nullable = false)
    private LocalDate firstDate;

    @Column(name = "gender", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    @Column(name = "picture_m")
    private String pictureM;

    @Column(name = "picture_w")
    private String pictureW;

    @Embedded
    private AuditEntity auditEntity;

    @Builder
    public Member(String uuid, String nickname, LocalDate firstDate, Gender gender, String pictureM,
                  String pictureW) {
        this.uuid = uuid;
        this.nickname = nickname;
        this.firstDate = firstDate;
        this.gender = gender;
        this.pictureM = pictureM;
        this.pictureW = pictureW;
        this.auditEntity = new AuditEntity();
    }

    public void update(Member user) {
        nickname = user.getNickname();
        firstDate = user.getFirstDate();
        gender = user.getGender();
    }

    public void updateImgUrls(List<String> imgUrls) {
        this.pictureM = imgUrls.get(0);
        this.pictureW = imgUrls.get(1);
    }

    public MemberDetailResponse toMemberDetailResponse() {
        return MemberDetailResponse.builder()
                                   .uuid(uuid)
                                   .nickname(nickname)
                                   .dDay(DAYS.between(firstDate, LocalDate.now()) + 1)
                                   .gender(gender.toString())
                                   .pictureM(pictureM)
                                   .pictureW(pictureW)
                                   .build();
    }
}
