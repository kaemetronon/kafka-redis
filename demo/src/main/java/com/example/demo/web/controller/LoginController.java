package com.example.demo.web.controller;

import com.example.demo.model.dto.auth.LoginDto;
import com.example.demo.model.dto.auth.TokenResponse;
import com.example.demo.security.JwtService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RequiredArgsConstructor
@RestController
@Api(value = "Login controller")
public class LoginController {

  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  @PostMapping("/login")
  @ApiOperation(value = "Login endpoint")
  public TokenResponse login(@RequestBody LoginDto loginDto) {
    Authentication authentication;
    try {
      authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
      log.info("Bad credentials, not authorised. DTO: {}", loginDto);
    } catch (BadCredentialsException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Имя или пароль неправильны", e);
    }
    String jwt = jwtService.generateToken((UserDetails) authentication.getPrincipal());

    return new TokenResponse(jwt);
  }
}
