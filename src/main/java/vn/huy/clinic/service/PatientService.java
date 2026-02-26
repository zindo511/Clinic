package vn.huy.clinic.service;

import org.springframework.data.domain.Page;
import vn.huy.clinic.dto.Appointment.AppointmentRequest;
import vn.huy.clinic.dto.Appointment.AppointmentResponse;
import vn.huy.clinic.dto.Patient.PatientResponse;
import vn.huy.clinic.model.Patient;

import java.util.List;

public interface PatientService {

    Page<Patient> getAllPatients(int page, int size, String sortBy, String sortDir);

    AppointmentResponse createAppointment(String username, AppointmentRequest request);

    List<PatientResponse> searchPatient(String keyword, String phone);

    PatientResponse getPatientById(Integer id);

    PatientResponse updatePatient(Integer id, vn.huy.clinic.dto.Patient.PatientRequest request);

    void deletePatient(Integer id);

    PatientResponse getProfile(String username);
}
