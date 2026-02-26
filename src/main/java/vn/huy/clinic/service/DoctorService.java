package vn.huy.clinic.service;


import org.springframework.data.domain.Page;
import vn.huy.clinic.dto.Appointment.AppointmentResponse;
import vn.huy.clinic.dto.Doctor.request.DoctorCreationRequest;
import vn.huy.clinic.dto.Doctor.request.DoctorUpdate;
import vn.huy.clinic.dto.Doctor.response.DoctorAppointmentResponse;
import vn.huy.clinic.dto.Doctor.response.DoctorResponse;
import vn.huy.clinic.dto.Doctor.response.ScheduleResponse;
import vn.huy.clinic.dto.Prescription.PrescriptionPatient;
import vn.huy.clinic.model.Doctor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface DoctorService {

    Page<DoctorResponse> searchDoctors(String keyword, Long specializationId, int page, int size, String sortBy, String sortDir);

    DoctorResponse getDoctorById(Integer id);

    List<LocalTime> getDoctorAvailability(Integer doctorId, LocalDate date);

    DoctorResponse getDoctorProfile();

    List<ScheduleResponse> getDoctorSchedule();

    Doctor updateDoctor(String username, DoctorUpdate doctorUpdate);

    DoctorResponse createDoctor(DoctorCreationRequest request);

    DoctorAppointmentResponse completeAppointment(Integer appointmentId, PrescriptionPatient prescriptionPatient);
}
