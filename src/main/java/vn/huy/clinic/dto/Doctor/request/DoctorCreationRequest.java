package vn.huy.clinic.dto.Doctor.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorCreationRequest {

    @NotBlank(message = "Họ đệm không được để trống")
    @Size(max = 50, message = "Họ đếm tôi đa 50 ký tự")
    private String firstName;

    @NotBlank(message = "Tên không được để trống")
    @Size(max = 50, message = "Tên tối đa 50 ký tự")
    private String lastName;

    @NotBlank(message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email quá dài")
    private String email;

    // Xử lý khoá ngoại (quan trọng)
    @NotNull(message = "Vui lòng chọn giới tính")
    private Long genderId;

    // Thay vì truyền List<Specialization>, ta truyền List ID các chuyên khoa
    @NotEmpty(message = "Bác sĩ phải có ít nhất 1 chuyên khoa")
    private List<Long> specializationIds;

    // Liên kết tài khoản User
    // Nếu tạo bác sĩ đã cho 1 user đã tồn tại
    @NotNull(message = "Cần liên kết với 1 tài khoản user")
    private Long userId;
}
