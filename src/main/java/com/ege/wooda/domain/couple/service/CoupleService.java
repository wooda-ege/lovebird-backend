package com.ege.wooda.domain.couple.service;

import com.ege.wooda.domain.couple.domain.Couple;
import com.ege.wooda.domain.couple.repository.CoupleRepository;
import com.ege.wooda.domain.profile.domain.Profile;
import com.ege.wooda.domain.profile.dto.param.ConnectCoupleParam;
import com.ege.wooda.domain.profile.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class CoupleService {
    private final ProfileRepository profileRepository;
    private final CoupleRepository coupleRepository;

    @Transactional
    public Couple getCoupleCode(Long memberId) {
        Couple couple=findByMemberId(memberId);

        codeReset(couple);

        return couple;
    }

    @Transactional
    public Long connectCouple(ConnectCoupleParam connectCoupleParam) {
        Couple couple = findByCoupleCode(connectCoupleParam.coupleCode());
        Long partnerId=couple.getMemberId();

        Profile self=profileRepository.findProfileByMemberId(connectCoupleParam.memberId()).orElseThrow(EntityNotFoundException::new);
        Profile partner=profileRepository.findProfileByMemberId(partnerId).orElseThrow(EntityNotFoundException::new);

        self.updateLinkedFlag(partnerId);
        partner.updateLinkedFlag(self.getId());

        couple.connectCouple(connectCoupleParam.coupleCode());
        couple.expireCode();

        return couple.getMemberId();
    }

    @Transactional(readOnly = true)
    public Couple findById(Long id) {
        return coupleRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Couple findByMemberId(Long id) {
        return coupleRepository.findByMemberId(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Couple findByCoupleCode(String coupleCode) {
        return coupleRepository.findByCoupleCode(coupleCode).orElseThrow(EntityNotFoundException::new);
    }

    private String generateCode() {
        int LENGTH = 8;

        Random random = new Random();
        StringBuilder oneTimePassword = new StringBuilder();
        for (int i = 0; i < LENGTH; i++) {
            int randomNumber = random.nextInt(10);
            oneTimePassword.append(randomNumber);
        }
        return oneTimePassword.toString().trim();
    }

    private void codeReset(Couple couple) {
        if (!couple.getUseFlag() && !isNull(couple.getCoupleCode()) && couple.isExpired()) {
                couple.deleteCoupleCode();
                couple.expireCode();
        }
        else {
            couple.updateCoupleCode(generateCode());
        }
    }
}
