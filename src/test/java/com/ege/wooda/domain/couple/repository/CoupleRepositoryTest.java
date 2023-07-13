package com.ege.wooda.domain.couple.repository;

import com.ege.wooda.domain.couple.domain.Couple;
import com.ege.wooda.domain.couple.service.CoupleService;
import com.ege.wooda.global.config.jpa.JpaConfig;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@Import(JpaConfig.class)
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class CoupleRepositoryTest {

    @Autowired
    CoupleRepository coupleRepository;

    @Mock
    CoupleService coupleService;

    @AfterEach
    void cleanUp(){
        coupleRepository.deleteAll();
    }

    @Test
    @DisplayName("Couple 코드를 생성한다.")
    public void save(){
        Couple couple=getCouple(coupleService.generateCode(), 2L);
        coupleRepository.save(couple);

        Couple existCouple=coupleRepository.findById(couple.getId()).orElseThrow(EntityNotFoundException::new);

        assertEquals(couple.getId(), existCouple.getId());
        assertEquals(couple.getMemberId(), existCouple.getMemberId());
        assertEquals(couple.getCoupleCode(), existCouple.getCoupleCode());
    }

    private Couple getCouple(String coupleCode, Long memberId){
        return Couple.builder()
                .memberId(memberId)
                .coupleCode(coupleCode)
                .build();
    }
}
