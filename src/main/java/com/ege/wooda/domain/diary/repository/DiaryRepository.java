package com.ege.wooda.domain.diary.repository;

import com.ege.wooda.domain.diary.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
}
