package vn.huy.clinic.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.huy.clinic.dto.Auth.request.AuthenticationRequest;
import vn.huy.clinic.dto.Auth.request.RegisterRequest;
import vn.huy.clinic.dto.ApiResponse;
import vn.huy.clinic.dto.Auth.response.AuthenticationResponse;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Quản lý đăng nhập")
public class AuthenticationController {

    private final AuthenticationService authService;

    // API ĐĂNG KÝ
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(@RequestBody RegisterRequest request) {
        AuthenticationResponse authResponse = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Đăng ký thành công", authResponse));
    }

    // API ĐĂNG NHẬP
    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse authResponse = authService.authenticate(request);
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", authResponse));
    }

    // REFRESH TOKEN
    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authService.refreshToken(request, response);
    }
}
