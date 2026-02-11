package vn.huy.clinic.dto.Appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.huy.clinic.model.Appointment;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentResponse {

    private Integer id;
    private String patientName;
    private String appointmentTime;
    private String status;
    private String reason;

    public static AppointmentResponse fromEntity(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .patientName(appointment.getPatient().getLastName() + " " + appointment.getPatient().getFirstName())
                .appointmentTime(appointment.getAppointmentDatetime().toLocalDate().toString())
                .status(appointment.getStatus().getStatusName())
                .reason(appointment.getReason())
                .build();
    }
}
