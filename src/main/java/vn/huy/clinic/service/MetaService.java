package vn.huy.clinic.service;

import vn.huy.clinic.model.AppointmentStatus;
import vn.huy.clinic.model.Specialization;

import java.util.List;

public interface MetaService {

    List<Specialization> getAllSpecializations();

    List<AppointmentStatus> getAllAppointmentStatuses();
}
