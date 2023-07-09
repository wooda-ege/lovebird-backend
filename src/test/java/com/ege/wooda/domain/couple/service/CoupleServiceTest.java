package com.ege.wooda.domain.couple.service;

import com.ege.wooda.domain.couple.domain.Couple;
import com.ege.wooda.domain.couple.dto.param.ConnectCoupleParam;
import com.ege.wooda.domain.couple.repository.CoupleRepository;
import com.ege.wooda.domain.member.domain.enums.Gender;
import com.ege.wooda.domain.profile.domain.Profile;
import com.ege.wooda.domain.profile.repository.ProfileRepository;
import com.ege.wooda.global.config.jpa.JpaConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@Import(JpaConfig.class)
@ExtendWith(MockitoExtension.class)
public class CoupleServiceTest {

    @InjectMocks
    private CoupleService coupleService;

    @Mock
    private CoupleRepository coupleRepository;

    @Mock
    private ProfileRepository profileRepository;

    @AfterEach
    public void cleanUp(){
        coupleRepository.deleteAll();
    }

    @Test
    @DisplayName("Couple 연동 테스트")
    public void connect() throws Exception{
        Couple couple=getCouple(generateCode(), 3L);
        Couple partner=getCouple(generateCode(), 7L);

        Profile selfP = getProfile(3L, "홍길동", getLocalDate("2023-06-01"), Gender.MALE);
        Profile partnerP = getProfile(7L, "홍길순", getLocalDate("2023-06-01"), Gender.FEMALE);

        Long mockId=1L;
        ConnectCoupleParam connectCoupleParam=new ConnectCoupleParam(3L, partner.getCoupleCode());

        ReflectionTestUtils.setField(couple, "id", mockId);

        given(profileRepository.findProfileByMemberId(3L))
                .willReturn(Optional.ofNullable(selfP));
        given(profileRepository.findProfileByMemberId(7L))
                .willReturn(Optional.ofNullable(partnerP));
        given(coupleRepository.findByCoupleCode(connectCoupleParam.coupleCode()))
                .willReturn(Optional.ofNullable(partner));

        Long partnerId=coupleService.connectCouple(connectCoupleParam);

        assertEquals(partnerId, partnerP.getMemberId());
        assertEquals(selfP.getLinkedFlag(), true);
        assertEquals(partnerP.getLinkedFlag(), true);

    }

    private String generateCode() {
        int LENGTH = 8;

        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        StringBuilder oneTimePassword = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());

            oneTimePassword.append(AlphaNumericString.charAt(index));
        }

        return oneTimePassword.toString().trim();
    }

    private Couple getCouple(String coupleCode, Long memberId){
        return Couple.builder()
                .memberId(memberId)
                .coupleCode(coupleCode)
                .build();
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
