package com.ege.wooda.domain.diary.repository;

import com.ege.wooda.domain.diary.domain.Diary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    @Query("select d from Diary d where memberId=?1")
    List<Diary> findByMemberId(Long id);
}
