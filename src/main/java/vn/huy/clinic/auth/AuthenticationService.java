package vn.huy.clinic.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.huy.clinic.config.JwtService;
import vn.huy.clinic.dto.Auth.request.AuthenticationRequest;
import vn.huy.clinic.dto.Auth.request.RegisterRequest;
import vn.huy.clinic.dto.Auth.response.AuthenticationResponse;
import vn.huy.clinic.exception.DuplicateResourceException;
import vn.huy.clinic.exception.InvalidDataException;
import vn.huy.clinic.model.Patient;
import vn.huy.clinic.model.Role;
import vn.huy.clinic.model.User;
import vn.huy.clinic.repository.PatientRepository;
import vn.huy.clinic.repository.RoleRepository;
import vn.huy.clinic.repository.UserRepository;
import vn.huy.clinic.token.Token;
import vn.huy.clinic.token.TokenRepository;
import vn.huy.clinic.token.TokenType;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    private final PatientRepository patientRepository;
    private final TokenRepository tokenRepository;

    // ĐĂNG KÝ
    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {

        // 1. Kiểm tra trùng lặp
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Tên đăng nhập đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email đã được sử dụng");
        }
        // Tạo role mặc định
        Role roleDefault = roleRepository.findByName("ROLE_PATIENT")
                .orElseThrow(() -> new InvalidDataException("Role không chính xác"));
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .isActive(true)
                .role(roleDefault)
                .build();

        // Lưu user vào database
        var savedUser = userRepository.save(user);

        // Lưu Patient
        Patient patient = new Patient();
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setUser(user);
        patient.setGender(request.getGender());
        patientRepository.save(patient);

        // Sinh token và trả về
        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, accessToken);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // ĐĂNG NHẬP
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Hàm này sẽ tự động check user/pass, nếu sai sẽ ném Exception
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Nếu chạy đến đây nghĩa là đăng nhập đúng -> Tạo token trả về
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidDataException("Tên đăng nhập không đúng"));

        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokedAllUserTokens(user);
        saveUserToken(user, accessToken);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void saveUserToken(User user, String accessToken) {
        var token = Token.builder()
                .user(user)
                .token(accessToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokedAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    // HÀM LÀM MỚI TOKEN (REFRESH TOKEN)
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        // 1. Lấy Header từ Authorization
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidDataException("Missing or invalid Authorization header");
        }

        refreshToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshToken);

        if (username != null) {
            var user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new InvalidDataException("Tên đăng nhập không đúng"));

            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokedAllUserTokens(user);
                saveUserToken(user, accessToken);
                AuthenticationResponse authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
                return;
            }
        }
        throw new InvalidDataException("Refresh token không chính xác hoặc đã hết hạn");
    }
}
