package vn.huy.clinic.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.huy.clinic.dto.Appointment.AppointmentResponse;
import vn.huy.clinic.exception.InvalidDataException;
import vn.huy.clinic.exception.ResourceNotFoundException;
import vn.huy.clinic.model.Appointment;
import vn.huy.clinic.model.AppointmentStatus;
import vn.huy.clinic.model.Doctor;
import vn.huy.clinic.model.Patient;
import vn.huy.clinic.repository.*;
import vn.huy.clinic.service.AppointmentService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentStatusRepository appointmentStatusRepository;

    @Override
    public AppointmentResponse getAppointment(Integer id) {
        Appointment appointment = appointmentRepository.findByIdWithPatientAndDoctor(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy cuộc hẹn nào với id: " + id));
        return AppointmentResponse.fromEntity(appointment);
    }

    @Override
    public List<AppointmentResponse> getDoctorAppointments(String username, String date, String statusName) {
        // 1. Tìm bác sĩ theo username
        Doctor doctor = doctorRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bác sĩ"));

        // 2. Parse ngày, nếu không truyền thì lấy hôm nay
        LocalDate localDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();

        // 3. Truy vấn và map về DTO
        return appointmentRepository.findDoctorAppointments(doctor.getId(), localDate, statusName)
                .stream()
                .map(AppointmentResponse::fromEntity)
                .toList();
    }

    @Override
    public List<AppointmentResponse> getPatientAppointments(String username) {
        // 1. Tìm bệnh nhân qua user
        Patient patient = patientRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bệnh nhân"));

        // 2. Truy vấn và map về DTO
        return appointmentRepository.findPatientAppointments(patient.getId())
                .stream()
                .map(AppointmentResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public AppointmentResponse cancelAppointment(Integer appointmentId, String username) {
        // 1. Tìm cuộc hẹn
        Appointment appointment = appointmentRepository.findByIdWithPatientAndDoctor(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy cuộc hẹn với id: " + appointmentId));

        // 2. Kiểm tra cuộc hẹn có thuộc về bệnh nhân đang login không
        if (!appointment.getPatient().getUser().getUsername().equals(username)) {
            throw new InvalidDataException("Bạn không có quyền hủy cuộc hẹn này");
        }

        // 3. Kiểm tra trạng thái — chỉ hủy được PENDING
        String currentStatus = appointment.getStatus().getStatusName();
        if (!"PENDING".equals(currentStatus)) {
            throw new InvalidDataException("Không thể hủy cuộc hẹn ở trạng thái: " + currentStatus);
        }

        // 4. Cập nhật trạng thái thành CANCELLED
        AppointmentStatus cancelledStatus = appointmentStatusRepository.findByStatusName("CANCELLED")
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy trạng thái CANCELLED"));

        appointment.setStatus(cancelledStatus);
        return AppointmentResponse.fromEntity(appointmentRepository.save(appointment));
    }

    @Override
    public List<AppointmentResponse> getPatientAppointmentByStatus(Integer patientId, String statusName) {
        List<Appointment> appointments = appointmentRepository.findPatientAppointmentByStatus(patientId, statusName);
        return appointments.stream()
                .map(AppointmentResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public AppointmentResponse rescheduleAppointment(Integer appointmentId, String username, LocalDateTime newDate) {
        Appointment appointment = appointmentRepository.findByIdByStatus(appointmentId, "PENDING")
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy cuộc hẹn nào"));
        String ownerUsername = appointment.getPatient().getUser().getUsername();
        if (!ownerUsername.equals(username)) {
            throw new InvalidDataException("Cuộc hẹn không thuộc về bạn");
        }
        if (!appointment.getAppointmentDatetime().isBefore(newDate)) {
            throw new InvalidDataException("Thời gian cập nhật cần phải ở tương lai");
        }

        boolean isBusy = appointmentRepository.existsByDoctorAndAppointmentDatetimeBetween(
                        appointment.getDoctor(),
                        newDate.minusMinutes(29),
                        newDate.plusMinutes(29)
                );
        if (isBusy) {
            throw new InvalidDataException("Bác sĩ đã kín lịch vào khung giờ này");
        }
        appointment.setAppointmentDatetime(newDate);
        return AppointmentResponse.fromEntity(appointmentRepository.save(appointment));
    }

}
