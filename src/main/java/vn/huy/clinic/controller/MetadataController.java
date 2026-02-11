package vn.huy.clinic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.huy.clinic.dto.ApiResponse;
import vn.huy.clinic.model.AppointmentStatus;
import vn.huy.clinic.model.Specialization;
import vn.huy.clinic.service.MetaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/meta")
@RequiredArgsConstructor
@Tag(name = "Metadata", description = "API lấy danh mục")
public class MetadataController {

    private final MetaService metaService;

    @GetMapping("/specializations")
    @Operation(summary = "Lấy danh sách chuyên khoa")
    public ResponseEntity<ApiResponse<List<Specialization>>> getSpecializations() {
        List<Specialization> specializations = metaService.getAllSpecializations();

        return ResponseEntity.ok(ApiResponse.success("Danh sách chuyên khoa", specializations));
    }

    @GetMapping("/appointment-statuses")
    @Operation(summary = "Lấy danh sách trạng thái lịch hẹn")
    public ResponseEntity<ApiResponse<List<AppointmentStatus>>> getStatuses() {
        List<AppointmentStatus> appointmentStatuses = metaService.getAllAppointmentStatuses();

        return ResponseEntity.ok(ApiResponse.success("Danh sách trạng thái lịch hẹn", appointmentStatuses));
    }
}
