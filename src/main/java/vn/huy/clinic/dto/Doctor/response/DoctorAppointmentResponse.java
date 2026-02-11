package vn.huy.clinic.dto.Doctor.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.huy.clinic.dto.MedicineResponse;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorAppointmentResponse {

    private String patientName;
    private String doctorName;
    private LocalDateTime appointmentDatetime;
    private String status;
    private String reason;
    private String doctorDiagnosis;
    private String note;
    private List<MedicineResponse> medicineResponses;
}
