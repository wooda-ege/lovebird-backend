package com.ege.wooda.domain.profile.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.ege.wooda.domain.member.domain.enums.Gender;
import com.ege.wooda.domain.profile.domain.Profile;
import com.ege.wooda.global.config.jpa.JpaConfig;

import jakarta.persistence.EntityNotFoundException;

@Import(JpaConfig.class)
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ProfileRepositoryTest {
    @Autowired
    private ProfileRepository profileRepository;

    @AfterEach
    void cleanup() {
        profileRepository.deleteAll();
    }

    @Test
    @DisplayName("Profile을 저정한다.")
    void save() {
        // given
        Profile profile = getProfile(1L, "홍길동", getLocalDate("2023-06-01"), Gender.MALE);
        profileRepository.save(profile);

        // when
        Profile saveProfile = profileRepository.findById(profile.getId())
                                               .orElseThrow(EntityNotFoundException::new);

        // then
        assertEquals(profile, saveProfile);
    }

    private Profile getProfile(Long memberId, String nickname, LocalDate firstDate, Gender gender) {
        return Profile.builder()
                      .memberId(memberId)
                      .nickname(nickname)
                      .firstDate(firstDate)
                      .gender(gender)
                      .build();
    }

    private LocalDate getLocalDate(String memoryDate) {
        return LocalDate.parse(memoryDate, DateTimeFormatter.ISO_DATE);
    }
}
