package vn.huy.clinic.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import vn.huy.clinic.dto.ApiResponse;
import vn.huy.clinic.dto.Appointment.AppointmentRequest;
import vn.huy.clinic.dto.Appointment.AppointmentResponse;
import vn.huy.clinic.dto.Patient.PatientRequest;
import vn.huy.clinic.dto.Patient.PatientResponse;
import vn.huy.clinic.model.Patient;
import vn.huy.clinic.service.PatientService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patient")
@RequiredArgsConstructor
@Tag(name = "Patient", description = "API về bệnh nhân")
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Patient>>> getListPatients(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "firstname") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        Page<Patient> patients = patientService.getAllPatients(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Danh sách bệnh nhân", patients));
    }

    @PostMapping("/create-appointment")
    public ResponseEntity<ApiResponse<AppointmentResponse>> createAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid AppointmentRequest request) {
        String username = userDetails.getUsername();

        AppointmentResponse appointment = patientService.createAppointment(username, request);
        return ResponseEntity.ok(ApiResponse.success("Tạo thành công cuộc hẹn", appointment));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PatientResponse>>> searchPatient(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String phone) {
        List<PatientResponse> patientResponses = patientService.searchPatient(keyword, phone);
        return ResponseEntity.ok(ApiResponse.success("Tìm kiếm bệnh nhân thành công", patientResponses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientById(@PathVariable Integer id) {
        PatientResponse patientResponse = patientService.getPatientById(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin bệnh nhân thành công", patientResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientResponse>> updatePatient(
            @PathVariable Integer id,
            @RequestBody @Valid PatientRequest request) {
        PatientResponse patientResponse = patientService.updatePatient(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin bệnh nhân thành công", patientResponse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePatient(@PathVariable Integer id) {
        patientService.deletePatient(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa bệnh nhân thành công", null));
    }
}
