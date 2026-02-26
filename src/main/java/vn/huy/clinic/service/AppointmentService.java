package vn.huy.clinic.service;

import vn.huy.clinic.dto.Appointment.AppointmentResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {

    // Lấy chi tiết 1 cuộc hẹn theo id
    AppointmentResponse getAppointment(Integer id);

    // Bác sĩ xem danh sách lịch hẹn của mình (lọc theo ngày và trạng thái)
    List<AppointmentResponse> getDoctorAppointments(String username, String date, String statusName);

    // Bệnh nhân xem danh sách lịch hẹn của mình
    List<AppointmentResponse> getPatientAppointments(String username);

    // Bệnh nhân hủy lịch hẹn
    AppointmentResponse cancelAppointment(Integer appointmentId, String username);

    // Tìm tất cả lịch hẹn của bệnh nhân theo trạng thái
    List<AppointmentResponse> getPatientAppointmentByStatus(Integer patientId, String statusName);

    // Cho phép bệnh nhân đổi lịch hẹn sang ngày/giờ khác
    AppointmentResponse rescheduleAppointment(Integer appointmentId, String username, LocalDateTime newDate);
}
