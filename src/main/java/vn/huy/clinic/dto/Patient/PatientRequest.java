package vn.huy.clinic.dto.Patient;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.huy.clinic.model.common.Gender;

import java.time.LocalDate;

@Getter
@Setter
public class PatientRequest {

  @NotBlank(message = "First name is required")
  private String firstName;

  @NotBlank(message = "Last name is required")
  private String lastName;

  private Gender gender;

  private LocalDate dob;

  @NotBlank(message = "Phone number is required")
  private String phone;

  private String address;
}
