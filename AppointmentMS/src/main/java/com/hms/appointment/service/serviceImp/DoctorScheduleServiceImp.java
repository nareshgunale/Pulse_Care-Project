package com.hms.appointment.service.serviceImp;

import com.hms.appointment.entity.DoctorSchedule;
import com.hms.appointment.exception.HMSException;
import com.hms.appointment.repository.DoctorScheduleRepository;
import com.hms.appointment.service.DoctorScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorScheduleServiceImp implements DoctorScheduleService {

    private final DoctorScheduleRepository doctorScheduleRepository;

    @Override
    public DoctorSchedule saveOrUpdate(DoctorSchedule schedule) {
        DoctorSchedule existing = doctorScheduleRepository
                .findByDoctorIdAndDayOfWeek(schedule.getDoctorId(), schedule.getDayOfWeek())
                .orElse(null);

        if (existing != null) {
            existing.setStartTime(schedule.getStartTime());
            existing.setEndTime(schedule.getEndTime());
            existing.setAvailable(schedule.isAvailable());
            return doctorScheduleRepository.save(existing);
        }

        return doctorScheduleRepository.save(schedule);
    }

    @Override
    public List<DoctorSchedule> findAll() {
        return doctorScheduleRepository.findAll();
    }

    @Override
    public List<DoctorSchedule> findByDoctorId(Long doctorId) {
        return doctorScheduleRepository.findAll()
                .stream()
                .filter(s -> s.getDoctorId().equals(doctorId))
                .toList();
    }

}
