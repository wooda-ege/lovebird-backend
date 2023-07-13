package com.ege.wooda.domain.couple.service;

import com.ege.wooda.domain.couple.domain.Couple;
import com.ege.wooda.domain.couple.exception.CodeExpiredException;
import com.ege.wooda.domain.couple.repository.CoupleRepository;
import com.ege.wooda.domain.profile.domain.Profile;
import com.ege.wooda.domain.couple.dto.param.ConnectCoupleParam;
import com.ege.wooda.domain.profile.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class CoupleService {
    private final ProfileRepository profileRepository;
    private final CoupleRepository coupleRepository;
    private static final int LENGTH=8;
    private static final String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "0123456789"
            + "abcdefghijklmnopqrstuvxyz";

    @Transactional
    public Couple getCoupleCode(Long memberId) {
        Couple couple=findByMemberId(memberId);

        codeReset(couple);

        return couple;
    }

    @Transactional
    public Long connectCouple(ConnectCoupleParam connectCoupleParam) throws CodeExpiredException {
        Couple couple = findByCoupleCode(connectCoupleParam.coupleCode());
        Long partnerId=couple.getMemberId();

        Profile self=findProfileByMemberId(connectCoupleParam.memberId()).orElseThrow(EntityNotFoundException::new);
        Profile partner=findProfileByMemberId(partnerId).orElseThrow(EntityNotFoundException::new);

        if(!couple.isExpired()){
            self.updateLinkedFlag(partnerId);
            partner.updateLinkedFlag(self.getMemberId());

            couple.connectCouple(connectCoupleParam.coupleCode());
            couple.expireCode();
        }
        else{
            throw new CodeExpiredException();
        }

        return partnerId;
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

    public String generateCode() {
        StringBuilder oneTimePassword = new StringBuilder(LENGTH);

        for (int i = 0; i < LENGTH; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());

            oneTimePassword.append(AlphaNumericString.charAt(index));
        }

        return oneTimePassword.toString().trim();
    }

    private Boolean checkUniqueness(String code){
        Couple couple=findByCoupleCode(code);
        if(!isNull(couple) && !couple.getUseCode()){
            return Boolean.FALSE;
        }
        else return Boolean.TRUE;
    }

    private void codeReset(Couple couple) {
        if (!couple.getUseCode() && !isNull(couple.getCoupleCode()) && couple.isExpired()) {
                couple.deleteCoupleCode();
                couple.expireCode();
        }
        else {
            String newCode=generateCode();
            while (!checkUniqueness(newCode)){
                newCode=generateCode();
            }
            couple.updateCoupleCode(newCode);
        }
    }

    private Optional<Profile> findProfileByMemberId(Long id){
        return profileRepository.findProfileByMemberId(id);
    }
}
