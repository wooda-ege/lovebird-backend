package com.ege.wooda.domain.couple.repository;

import com.ege.wooda.domain.couple.domain.Couple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoupleRepository extends JpaRepository<Couple, Long> {
    Optional<Couple> findCoupleById(Long id);
    Optional<Couple> findByMemberId(Long memberId);
    Optional<Couple> findByCoupleCode(String coupleCode);
}
