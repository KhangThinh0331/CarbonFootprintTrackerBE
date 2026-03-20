package com.khangthinh.carbonfootprinttracker.exception;

import com.khangthinh.carbonfootprinttracker.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Xử lý các lỗi logic nghiệp vụ do chúng ta chủ động ném ra (RuntimeException)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value()) // HTTP 400
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage()) // Lấy message từ throw new RuntimeException("...")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 2. Xử lý lỗi không tìm thấy tài nguyên (Ví dụ: truyền sai ID)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value()) // HTTP 404
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(Exception ex, WebRequest request) {
        String message = "Tên đăng nhập hoặc mật khẩu không chính xác!";
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        // Kiểm tra xem lỗi gốc (Cause) có phải là DisabledException hay không
        // Vì Spring Security bọc DisabledException bên trong InternalAuthenticationServiceException
        Throwable cause = ex.getCause();
        if (ex instanceof DisabledException || cause instanceof DisabledException) {
            message = (cause != null) ? cause.getMessage() : ex.getMessage();
            status = HttpStatus.FORBIDDEN; // Trả về 403 cho tài khoản chưa xác minh
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    // 3. Xử lý CẤP ĐỘ CAO NHẤT: Bắt tất cả các lỗi chưa được lường trước (Lỗi Server)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {

        // Log lỗi ra console để dev biết đường sửa (có thể dùng Slf4j)
        ex.printStackTrace();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value()) // HTTP 500
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Đã xảy ra lỗi hệ thống. Vui lòng liên hệ Admin!") // Giấu chi tiết lỗi thật đi để bảo mật
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 4. Xử lý lỗi Validation (Dữ liệu đầu vào không hợp lệ)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();

        // Trích xuất tất cả các lỗi từ Exception
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField(); // Tên trường (vd: quantity)
            String errorMessage = error.getDefaultMessage();    // Lời nhắn (vd: Số lượng không được là số âm)
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value()) // HTTP 400
                .error("Validation Failed")
                .message("Dữ liệu đầu vào không hợp lệ. Vui lòng kiểm tra lại!")
                .validationErrors(errors) // Đưa danh sách lỗi vào đây
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}