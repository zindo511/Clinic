package vn.huy.clinic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import vn.huy.clinic.dto.ApiResponse;
import vn.huy.clinic.dto.Appointment.AppointmentResponse;
import vn.huy.clinic.service.AppointmentService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Quản lý lịch hẹn")
public class AppointmentController {

    private final AppointmentService appointmentService;

    // --- DÙNG CHUNG ---

    @GetMapping("/{id}")
    @Operation(summary = "Xem chi tiết một cuộc hẹn theo ID")
    public ResponseEntity<ApiResponse<AppointmentResponse>> getAppointment(@PathVariable Integer id) {
        AppointmentResponse response = appointmentService.getAppointment(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin cuộc hẹn thành công", response));
    }

    // --- BỆNH NHÂN ---

    @GetMapping("/mine")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Bệnh nhân xem danh sách lịch hẹn của mình")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getMyAppointments(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<AppointmentResponse> responses = appointmentService.getPatientAppointments(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch hẹn thành công", responses));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Bệnh nhân hủy lịch hẹn (chỉ hủy được khi trạng thái PENDING)")
    public ResponseEntity<ApiResponse<AppointmentResponse>> cancelAppointment(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        AppointmentResponse response = appointmentService.cancelAppointment(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Hủy lịch hẹn thành công", response));
    }

    @GetMapping("/{id}/appointment")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getPatientAppointmentByStatus(
            @PathVariable Integer patientId,
            @RequestParam(required = false) String statusName
    ) {
        List<AppointmentResponse> responses = appointmentService.getPatientAppointmentByStatus(patientId, statusName);
        return ResponseEntity.ok(ApiResponse.success("Danh sách cuộc hẹn theo trạng thái", responses));
    }

    @PutMapping("/{appointmentId}/updateDatePatient")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Cập nhật lịch cho bệnh nhân")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateDatePatient(
            @PathVariable Integer appointmentId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newDate
    ) {
        String username = userDetails.getUsername();
        AppointmentResponse responses = appointmentService.rescheduleAppointment(appointmentId, username, newDate);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật giờ thành công", responses));
    }

    // --- BÁC SĨ ---

    @GetMapping("/doctor/schedule")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Bác sĩ xem lịch hẹn của mình (lọc theo ngày và trạng thái)")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getDoctorAppointments(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String status) {
        List<AppointmentResponse> responses = appointmentService.getDoctorAppointments(
                userDetails.getUsername(), date, status);
        return ResponseEntity.ok(ApiResponse.success("Lấy lịch hẹn thành công", responses));
    }
}
