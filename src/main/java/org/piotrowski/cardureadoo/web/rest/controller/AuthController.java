package org.piotrowski.cardureadoo.web.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.web.dto.user.LoginRequest;
import org.piotrowski.cardureadoo.web.dto.user.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    // POST - login endpoint
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );

            String username = auth.getName();
            Set<String> roles = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            return new LoginResponse(username, roles);
        } catch (BadCredentialsException ex) {
            throw ex;
        }
    }
}
