package org.piotrowski.cardureadoo.web.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.service.UserApplicationService;
import org.piotrowski.cardureadoo.infrastructure.security.session.InMemoryTokenStore;
import org.piotrowski.cardureadoo.web.dto.user.ChangePasswordRequest;
import org.piotrowski.cardureadoo.web.dto.user.ChangePasswordResponse;
import org.piotrowski.cardureadoo.web.dto.user.LoginRequest;
import org.piotrowski.cardureadoo.web.dto.user.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "Auth", description = "Authentication and password management operations")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final InMemoryTokenStore tokenStore;
    private final UserApplicationService userApplicationService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Authenticate user", description = "Authenticates a user using username and password and returns a session token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password", content = @Content)
    })
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );

            UserDetails principal = (UserDetails) auth.getPrincipal();

            String username = principal.getUsername();
            Set<String> roles = principal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            String token = tokenStore.createSession(principal);

            return new LoginResponse(username, roles, token);
        } catch (BadCredentialsException ex) {
            throw ex;
        }
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Change user password", description = "Changes the user's password after providing the correct current password.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Invalid current password", content = @Content)
    })
    public ChangePasswordResponse resetPassword(@RequestBody @Valid ChangePasswordRequest request) {
        userApplicationService.changePassword(request.username(), request.oldPassword(), request.newPassword());
        return new ChangePasswordResponse(request.username());
    }
}
