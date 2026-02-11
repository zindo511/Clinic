package vn.huy.clinic.dto.Doctor.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.huy.clinic.model.common.Gender;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorUpdate {
    private String firstName;

    private String lastName;

    private Gender gender;

    private String bio;

    private String phone;

    @Email(message = "Email không đúng định dạng")
    private String email;
}
