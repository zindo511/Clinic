package vn.huy.clinic.dto.Appointment;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {

    @Min(value = 1, message = "id của bác sĩ phải lớn hơn 1")
    private Integer doctorId;

    @Future(message = "Thời gian phải lớn hơn thời gian hiện tại")
    private LocalDateTime appointmentDatetime;

    @NotBlank(message = "Lý do đi khám bệnh là gì")
    private String reason;
}
