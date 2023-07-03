package com.ege.wooda.domain.profile.service;

import static java.util.Objects.isNull;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ege.wooda.domain.profile.domain.Profile;
import com.ege.wooda.domain.profile.dto.param.ConnectCoupleParam;
import com.ege.wooda.domain.profile.repository.ProfileRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoupleService {
    private final ProfileRepository profileRepository;

    @Transactional
    public Profile updateCoupleCode(Long memberId) {
        Profile profile = findByMemberId(memberId);
        profile.updateCoupleCode(generateCode());

        scheduleCodeReset(profile);

        return profile;
    }

    @Transactional
    public Long connectCouple(ConnectCoupleParam connectCoupleParam) {
        Profile self = findByMemberId(connectCoupleParam.memberId());
        Profile partner = findByCoupleCode(connectCoupleParam.coupleCode());

        self.connectCouple(connectCoupleParam.coupleCode());
        partner.connectCouple(connectCoupleParam.coupleCode());

        return partner.getMemberId();
    }

    @Transactional(readOnly = true)
    public Profile findByMemberId(Long memberId) {
        return profileRepository.findProfileByMemberId(memberId).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Profile findByCoupleCode(String coupleCode) {
        return profileRepository.findProfileByCoupleCode(coupleCode).orElseThrow(EntityNotFoundException::new);
    }

    private String generateCode() {
        return UUID.randomUUID().toString()
                   .replaceAll("-", "")
                   .substring(0, 8);
    }

    private void scheduleCodeReset(Profile profile) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.schedule(() -> {
            if (!profile.getLinkedFlag() && !isNull(profile.getCoupleCode())) {
                profile.deleteCoupleCode();
            }
        }, 24, TimeUnit.HOURS);
        executor.shutdown();
    }
}
