package com.ege.wooda.domain.profile.domain;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import com.ege.wooda.domain.member.domain.enums.Gender;
import com.ege.wooda.domain.profile.domain.enums.Anniversary;
import com.ege.wooda.domain.profile.dto.response.ProfileDetailResponse;
import com.ege.wooda.global.audit.AuditEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "first_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate firstDate;

    @Column(name = "gender", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    @Column(name = "couple_code")
    private String coupleCode;

    @Column(name = "linked_flag", nullable = false)
    private Boolean linkedFlag;

    @Column(name = "image_url")
    private String imageUrl;

    @ElementCollection
    @CollectionTable(name = "anniversary", joinColumns = @JoinColumn(name = "profile_id"))
    @MapKeyColumn(name = "anniversary_kind")
    @Column(name = "anniversary_day")
    Map<Anniversary, LocalDate> anniversaryMap;

    @Embedded
    private AuditEntity auditEntity;

    @Builder
    public Profile(Long memberId, String nickname, LocalDate firstDate, Gender gender) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.firstDate = firstDate;
        this.gender = gender;
        this.linkedFlag = Boolean.FALSE;
        this.anniversaryMap = new EnumMap<>(Anniversary.class);
        this.auditEntity = new AuditEntity();

        initAnniversaryMap();
    }

    public void initAnniversaryMap() {
        Anniversary[] anniversaries = Anniversary.values();

        anniversaryMap.put(Anniversary.FIRST_DATE, this.firstDate);
        anniversaryMap.put(Anniversary.ONE_HUNDRED, this.firstDate.plusDays(100));
        anniversaryMap.put(Anniversary.TWO_HUNDREDS, this.firstDate.plusDays(200));
        anniversaryMap.put(Anniversary.THREE_HUNDREDS, this.firstDate.plusDays(300));

        for(int i=4 ; i<anniversaries.length ; i++) {
            anniversaryMap.put(anniversaries[i], this.firstDate.plusYears(i-2));
        }
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void deleteImageUrl() {
        this.imageUrl = null;
    }

    public void update(Profile profile) {
        this.nickname = profile.getNickname();
        this.gender = profile.getGender();

        if(!this.firstDate.equals(profile.getFirstDate())) {
            this.firstDate = profile.getFirstDate();
            initAnniversaryMap();
        }
    }

    public ProfileDetailResponse toProfileDetailResponse() {
        return ProfileDetailResponse.builder()
                                   .memberId(memberId)
                                   .nickname(nickname)
                                   .anniversaryList(anniversaryMap)
                                   .gender(gender.toString())
                                   .profileImageUrl(imageUrl)
                                   .build();
    }
}
