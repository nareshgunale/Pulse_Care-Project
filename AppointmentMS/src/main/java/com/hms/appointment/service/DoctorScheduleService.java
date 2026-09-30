package com.hms.appointment.service;

import com.hms.appointment.entity.DoctorSchedule;

import java.util.List;

public interface DoctorScheduleService {
    DoctorSchedule saveOrUpdate(DoctorSchedule schedule);

    List<DoctorSchedule> findAll();

    List<DoctorSchedule> findByDoctorId(Long doctorId);
}
