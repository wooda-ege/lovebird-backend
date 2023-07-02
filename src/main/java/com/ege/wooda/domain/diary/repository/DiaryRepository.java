package com.ege.wooda.domain.diary.repository;

import com.ege.wooda.domain.diary.domain.Diary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    List<Diary> findDiariesByMemberId(Long memberId);
    Optional<Diary> findDiariesByMemberIdAndId(Long memberId, Long id);
}
