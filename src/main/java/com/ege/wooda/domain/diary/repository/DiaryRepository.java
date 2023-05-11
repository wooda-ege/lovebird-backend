package com.ege.wooda.domain.diary.repository;

import com.ege.wooda.domain.diary.dao.Diary;
import org.springframework.data.repository.CrudRepository;

public interface DiaryRepository extends CrudRepository<Diary, Long> {
}
