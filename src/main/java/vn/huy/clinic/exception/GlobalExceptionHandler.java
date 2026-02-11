package vn.huy.clinic.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 1. Xử lý VALIDATION (400)
    @ExceptionHandler({MethodArgumentNotValidException.class,
            ConstraintViolationException.class, MissingServletRequestParameterException.class})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Handle Bad Request",
                                    summary = "Handle Validation Exception",
                                    value = """
                                            {
                                                 "timestamp": "2024-04-07T11:38:56.368+00:00",
                                                 "status": 400,
                                                 "path": "/api/v1/users",
                                                 "error": "Invalid Payload",
                                                 "message": "Email không được để trống"
                                             }
                                            """
                            ))})
    })
    public ResponseEntity<ErrorResponse> handleValidationException(Exception e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(BAD_REQUEST.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setError("Invalid Payload");

        String message = e.getMessage();

        // Tối ưu: Lấy message chính xác từ annotation (@NotNull, @Size ...)
        if (e instanceof MethodArgumentNotValidException ex) {
            // Lấy lỗi đầu tiên tìm được để trả về cho gọn
            if (ex.getBindingResult().hasErrors()) {
                message = ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
            }
        } else if (e instanceof ConstraintViolationException) {
            message = message.substring(message.indexOf(" ") + 1);
        }

        errorResponse.setMessage(message);
        return ResponseEntity.status(NOT_FOUND).body(errorResponse);
    }

    // --- 2. Xử lý AUTHENTICATION (401) ---
    @ExceptionHandler({InternalAuthenticationServiceException.class, BadCredentialsException.class})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "401 Response",
                                    summary = "Handle Login Fail",
                                    value = """
                                            {
                                              "timestamp": "2023-10-19T06:07:35.321+00:00",
                                              "status": 401,
                                              "path": "/api/v1/auth/login",
                                              "error": "Unauthorized",
                                              "message": "Username or password is incorrect"
                                            }
                                            """
                            ))})
    })
    public ResponseEntity<ErrorResponse> handleAuthenticationException(Exception e, WebRequest request) {
        // 1. ghi log cho dev xem
        if (e instanceof InternalAuthenticationServiceException) {
            log.error("Lỗi hệ thống khi login: ", e);
        }
        else log.warn("Đăng nhập thất bại: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(UNAUTHORIZED.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setError(UNAUTHORIZED.getReasonPhrase());
        errorResponse.setMessage("Username or password is incorrect");

        return ResponseEntity.status(UNAUTHORIZED).body(errorResponse);
    }

    // 3. Xử lý quyền truy cập (403)
    @ExceptionHandler({ForbiddenException.class, AccessDeniedException.class})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "403 Response",
                                    summary = "Handle Forbidden",
                                    value = "..."
                            ))})
    })
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(Exception e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(FORBIDDEN.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setError(FORBIDDEN.getReasonPhrase());
        errorResponse.setMessage("Bạn không có quyền truy cập vào tài nguyên này");
        return ResponseEntity.status(FORBIDDEN).body(errorResponse);
    }

    // 4. Xử lý không tìm thấy (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "404 Response",
                                    summary = "Handle exception when resource not found",
                                    value = """
                                            {
                                              "timestamp": "2023-10-19T06:07:35.321+00:00",
                                              "status": 404,
                                              "path": "/api/v1/...",
                                              "error": "Not Found",
                                              "message": "{data} not found"
                                            }
                                            """
                            ))})
    })
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(Exception e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(NOT_FOUND.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setError(NOT_FOUND.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());
        return ResponseEntity.status(NOT_FOUND).body(errorResponse);
    }

    // --- 5. XỬ LÝ TRÙNG LẶP / CONFLICT (409) ---
    @ExceptionHandler({InvalidDataException.class, DuplicateResourceException.class})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "409 Response", summary = "Handle Conflict", value = "..."))})
    })
    public ResponseEntity<ErrorResponse> handleConflictException(Exception e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setStatus(CONFLICT.value());
        errorResponse.setError(CONFLICT.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());

        return ResponseEntity.status(CONFLICT).body(errorResponse);
    }

    // 6. Xử lý lỗi hệ thống (500)
    @ExceptionHandler(Exception.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "500 Response", summary = "Handle Internal Error", value = "..."))})
    })
    public ResponseEntity<ErrorResponse> handleInternalServerException(Exception e, WebRequest request) {
        log.error("Internal error at {}: ", request.getDescription(false), e);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setStatus(INTERNAL_SERVER_ERROR.value());
        errorResponse.setError(INTERNAL_SERVER_ERROR.getReasonPhrase());
        // KHÔNG trả e.getMessage() cho client vì lý do bảo mật
        errorResponse.setMessage("Lỗi hệ thống nội bộ, vui lòng thử lại sau!");
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
