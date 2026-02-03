package vn.hoidanit.jobhunter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.dto.LoginDTO;
import vn.hoidanit.jobhunter.util.SecurityUtil;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginDTO> login(@Valid @RequestBody LoginDTO loginDto) {
        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken 
            = new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
 
        // Xác thực người dùng => cần viết hàm loadUserByUsername
        // authentication khong luu mat khau nguoi dung sau khi dang nhap thanh cong
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // Create a token
        // authentication khong luu mat khau nguoi dung sau khi dang nhap thanh cong
        this.securityUtil.createToken(authentication);

        return ResponseEntity.ok().body(loginDto);
    }
}
