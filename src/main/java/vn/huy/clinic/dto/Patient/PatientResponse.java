package vn.huy.clinic.dto.Patient;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import vn.huy.clinic.model.Patient;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientResponse {

    private Integer id;
    private String firstName;
    private String lastName;
    private String gender;
    private String dob;
    private String phone;
    private String address;

    public static PatientResponse fromEntity(Patient patient) {
        if (patient == null) return null;
        return PatientResponse.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .gender(patient.getGender().toString())
                .dob(patient.getDob().toString())
                .phone(patient.getPhone())
                .address(patient.getAddress())
                .build();
    }
}
