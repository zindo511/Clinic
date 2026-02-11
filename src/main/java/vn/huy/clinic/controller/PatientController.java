package vn.huy.clinic.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import vn.huy.clinic.dto.ApiResponse;
import vn.huy.clinic.dto.Appointment.AppointmentRequest;
import vn.huy.clinic.dto.Appointment.AppointmentResponse;
import vn.huy.clinic.exception.ResourceNotFoundException;
import vn.huy.clinic.model.Appointment;
import vn.huy.clinic.model.Patient;
import vn.huy.clinic.model.User;
import vn.huy.clinic.repository.PatientRepository;
import vn.huy.clinic.repository.UserRepository;
import vn.huy.clinic.service.PatientService;

@RestController
@RequestMapping("/api/v1/patient")
@RequiredArgsConstructor
@Tag(name = "Patient", description = "API về bệnh nhân")
public class PatientController {

    private final PatientService patientService;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Patient>>> getListPatients(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstname") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir
    ) {
        Page<Patient> patients = patientService.getAllPatients(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Danh sách bệnh nhân", patients));
    }

    @PostMapping("/create-appointment")
    public ResponseEntity<ApiResponse<AppointmentResponse>> createAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid AppointmentRequest request
    ) {
        String username = userDetails.getUsername();

        AppointmentResponse appointment = patientService.createAppointment(username, request);
        return ResponseEntity.ok(ApiResponse.success("Tạo thành công cuộc hẹn", appointment));
    }
}
