package vn.huy.clinic.service;

import org.springframework.data.domain.Page;
import vn.huy.clinic.dto.Appointment.AppointmentRequest;
import vn.huy.clinic.dto.Appointment.AppointmentResponse;
import vn.huy.clinic.model.Appointment;
import vn.huy.clinic.model.Patient;

public interface PatientService {

    Page<Patient> getAllPatients(int page, int size, String sortBy, String sortDir);

    AppointmentResponse createAppointment(String username, AppointmentRequest request);
}
