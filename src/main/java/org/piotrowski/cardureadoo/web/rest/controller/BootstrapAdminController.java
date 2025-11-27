package org.piotrowski.cardureadoo.web.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.service.UserApplicationService;
import org.piotrowski.cardureadoo.domain.security.UserRole;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.entities.UserEntity;
import org.piotrowski.cardureadoo.web.dto.user.CreateUserRequest;
import org.piotrowski.cardureadoo.web.dto.user.CreateUserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/bootstrap")
@RequiredArgsConstructor
@Validated
@Tag(name = "Bootstrap", description = "Initial administrator setup")
public class BootstrapAdminController {

    private final UserApplicationService usersService;

    @Value("${app.security.bootstrap.token}")
    private String setupToken;

    @PostMapping(path = "/admin")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create administrator users", description = "Creates administrator account using a bootstrap token.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Administrator created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid setup token or input data", content = @Content)
    })
    public CreateUserResponse createFirstAdmin(
            @Parameter(description = "Configuration token required to create the administrator", required = true)
            @RequestHeader("X-Setup-Token") String token,
            @RequestBody @Valid CreateUserRequest req) {

        if (setupToken == null || setupToken.isBlank() || !setupToken.equals(token)) {
            throw new SecurityException("Invalid setup token");
        }

        UserEntity u = usersService.createUser(req.username(), req.password(), Set.of(UserRole.ADMIN));
        return new CreateUserResponse(u.getId(), u.getUsername(), Set.of(UserRole.ADMIN.name()));
    }
}
