package com.ege.wooda.domain.calendar.service;

import com.ege.wooda.domain.calendar.domain.Calendar;
import com.ege.wooda.domain.calendar.dto.request.CalendarCreateRequest;
import com.ege.wooda.domain.calendar.dto.request.CalendarUpdateRequest;
import com.ege.wooda.domain.calendar.repository.CalendarRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final CalendarRepository calRepository;

    @Transactional
    public Long save(CalendarCreateRequest calendarCreateRequest) throws IOException {
        System.out.println(calendarCreateRequest.endDate());
        Calendar calendar = calRepository.save(calendarCreateRequest.toEntity());

        return calendar.getId();
    }

    @Transactional
    public Long update(Long id, CalendarUpdateRequest calendarUpdateRequest) throws IOException{
        Calendar calendar=findById(id);
        calendar.updateCalendar(calendarUpdateRequest.toEntity());

        return id;
    }

    @Transactional
    public void delete(Long id){
        Calendar calendar=findById(id);
        calRepository.delete(calendar);
    }

    @Transactional(readOnly = true)
    public Calendar findById(Long id){ return calRepository.findById(id).orElseThrow(EntityNotFoundException::new);}

    @Transactional(readOnly = true)
    public List<Calendar> findByMemberId(Long id) {return calRepository.findByMemberId(id);}

}
