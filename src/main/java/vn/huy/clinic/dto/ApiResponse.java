package vn.huy.clinic.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    // 1. Các trường chung
    private int code;
    private String message;
    private String timestamp;

    // 2. Trường của Success
    private T data;

    // 3. Trường của error
    private String error;
    private String path;

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    public static <T> ApiResponse<T> error(int code, String message, String errorType, String path) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .error(errorType)
                .path(path)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }
}
