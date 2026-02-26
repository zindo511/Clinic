package vn.huy.clinic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import vn.huy.clinic.dto.Doctor.request.DoctorCreationRequest;
import vn.huy.clinic.dto.ApiResponse;
import vn.huy.clinic.dto.Doctor.request.DoctorUpdate;
import vn.huy.clinic.dto.Doctor.response.DoctorAppointmentResponse;
import vn.huy.clinic.dto.Doctor.response.DoctorResponse;
import vn.huy.clinic.dto.Doctor.response.ScheduleResponse;
import vn.huy.clinic.dto.Prescription.PrescriptionPatient;
import vn.huy.clinic.model.Doctor;
import vn.huy.clinic.service.DoctorService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctors", description = "Quản lý thông tin Bác sĩ")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    @Operation(summary = "Lấy danh sách bác sĩ (có phân trang & tìm kiếm)")
    public ResponseEntity<ApiResponse<Page<DoctorResponse>>> getDoctors(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long specializationId,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    ) {
        Page<DoctorResponse> doctorResponsePage = doctorService.searchDoctors(keyword, specializationId, page, size, sortBy, sortDir);

        return ResponseEntity.ok(ApiResponse.success("Lấy bác sĩ thành công", doctorResponsePage));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Xem chi tiết một bác sĩ")
    public ResponseEntity<ApiResponse<DoctorResponse>> getDoctorById(@PathVariable @Min(1) Integer id) {
        DoctorResponse doctor = doctorService.getDoctorById(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy thành công bác sĩ", doctor));
    }

    @GetMapping("{id}/availability")
    @Operation(summary = "Danh sách các khung giờ còn trống của bác sĩ")
    public ResponseEntity<ApiResponse<List<LocalTime>>> getDoctorByIdAndAvailability(
            @PathVariable @Min(1) Integer id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<LocalTime> times = doctorService.getDoctorAvailability(id, date);
        String message = times.isEmpty()
                ? "Bác sĩ hiện không có lịch trống trong ngày này"
                : "Danh sách các lịch trống trong ngày của bác sĩ";
        return ResponseEntity.ok(ApiResponse.success(message, times));
    }

    @GetMapping("/me")
    @Operation(summary = "Lấy thông tin hồ sơ của bác sĩ đang đăng nhập")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<DoctorResponse>> getDoctorProfile() {
        DoctorResponse profile = doctorService.getDoctorProfile();
        return ResponseEntity.ok(ApiResponse.success("Lấy hồ sơ cá nhân thành công", profile));
    }

    @GetMapping("/me/schedules")
    @Operation(summary = "Lấy lịch làm việc của bác sĩ đang đăng nhập")
    @PreAuthorize("hasRole('DOCTOR'))")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getDoctorSchedules() {
        List<ScheduleResponse> responses = doctorService.getDoctorSchedule();
        return ResponseEntity.ok(ApiResponse.success("Lấy lịch làm việc cá nhân thành công", responses));
    }
    // ============ ADMIN ONLY ===========
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo hồ sơ bác sĩ (Admin)")
    public ResponseEntity<ApiResponse<DoctorResponse>> createDoctor(@RequestBody DoctorCreationRequest request) {
        DoctorResponse response = doctorService.createDoctor(request);
        return ResponseEntity.ok(ApiResponse.success("Đã tạo thành công bác sĩ", response));
    }

    @PutMapping
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Cập nhật thông tin bác sĩ")
    public ResponseEntity<ApiResponse<Doctor>> updateDoctorInfo(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody DoctorUpdate doctorUpdate
    ) {

        String username = userDetails.getUsername();
        Doctor doctor = doctorService.updateDoctor(username, doctorUpdate);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thành công", doctor));
    }

    @PutMapping("/{appointmentId}/complete")
    public ResponseEntity<ApiResponse<DoctorAppointmentResponse>> completeAppointment(
            @PathVariable Integer appointmentId,
            @RequestBody @Valid PrescriptionPatient prescriptionPatient
    ) {
        DoctorAppointmentResponse response = doctorService.completeAppointment(appointmentId, prescriptionPatient);
        return ResponseEntity.ok(ApiResponse.success("Hoàn thành cuộc hẹn", response));
    }
}
