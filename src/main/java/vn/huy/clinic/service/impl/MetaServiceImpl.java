package vn.huy.clinic.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.huy.clinic.model.AppointmentStatus;
import vn.huy.clinic.model.Specialization;
import vn.huy.clinic.repository.AppointmentStatusRepository;
import vn.huy.clinic.repository.SpecializationRepository;
import vn.huy.clinic.service.MetaService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MetaServiceImpl implements MetaService {

    private final SpecializationRepository specializationRepository;
    private final AppointmentStatusRepository appointmentStatusRepository;

    @Override
    public List<Specialization> getAllSpecializations() {
        return specializationRepository.findAll();
    }

    @Override
    public List<AppointmentStatus> getAllAppointmentStatuses() {
        return appointmentStatusRepository.findAll();
    }
}
