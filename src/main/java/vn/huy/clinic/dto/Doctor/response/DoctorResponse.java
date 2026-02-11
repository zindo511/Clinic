package vn.huy.clinic.dto.Doctor.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.huy.clinic.model.Doctor;
import vn.huy.clinic.model.Specialization;
import vn.huy.clinic.model.common.Gender;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoctorResponse {

    private Integer id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private String phone;
    private String bio;
    private List<String> specializationNames;

    // map to dto
    public static DoctorResponse fromEntity(Doctor doctor) {
        if (doctor == null) return null;
        return DoctorResponse.builder()
                .id(doctor.getId())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .gender(doctor.getGender())
                .phone(doctor.getPhone())
                .bio(doctor.getBio())
                .specializationNames(
                        doctor.getSpecializations() != null ?
                        doctor.getSpecializations().stream()
                                .map(Specialization::getName)
                                .toList() : null
                )
                .build();
    }
}
