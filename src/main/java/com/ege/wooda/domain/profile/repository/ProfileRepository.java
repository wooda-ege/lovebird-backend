package com.ege.wooda.domain.profile.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ege.wooda.domain.profile.domain.Profile;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findProfileByMemberId(Long memberId);
    Optional<Profile> findProfileByCoupleCode(String coupleCode);
    List<Profile> findProfileByCoupleCodeNotNullAndLinkedFlagIsFalse();
}
