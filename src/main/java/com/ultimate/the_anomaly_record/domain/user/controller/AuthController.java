package com.ultimate.the_anomaly_record.domain.user.controller;

import com.ultimate.the_anomaly_record.domain.user.dto.UserRequest;
import com.ultimate.the_anomaly_record.domain.user.dto.UserResponse;
import com.ultimate.the_anomaly_record.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v0/user")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    /**
     * 회원가입
     */
    @Operation(
            summary = "회원가입 API",
            description = "이메일 인증 완료 후 회원가입을 진행합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (이메일 인증 실패, 중복 계정 등)"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid UserRequest.SignupRequest request) {
        Long userId = userService.signup(request);
        return ResponseEntity.ok(
                Map.of(
                        "userId", userId,
                        "message", "회원가입 완료"
                )
        );
    }

    /**
     * 로그인
     */
    @Operation(
            summary = "로그인 API",
            description = "이메일과 비밀번호로 로그인합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 실패 (인증 불가)"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/login")
    public ResponseEntity<UserResponse.LoginResponse> login(@RequestBody @Valid UserRequest.LoginRequest request) {
        UserResponse.LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
