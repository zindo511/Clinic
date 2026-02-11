package vn.huy.clinic.dto.Prescription;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.huy.clinic.dto.MedicineResponse;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionPatient {

    @NotBlank(message = "Bác sĩ bổ sung chẩn đoán cho bệnh nhân")
    private String doctorDiagnosis;

    @NotBlank(message = "Lưu ý bác sĩ dành cho bệnh nhân")
    private String note;

    @NotEmpty(message = "Bác sĩ bổ sung đơn thuốc cho bệnh nhân")
    private List<MedicineResponse> medicineResponses;
}
