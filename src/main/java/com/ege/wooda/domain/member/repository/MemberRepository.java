package com.ege.wooda.domain.member.repository;

import com.ege.wooda.domain.member.domain.Member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findMemberByNickname(String nickname);
    Optional<Member> findMemberByUuid(String uuid);
}
