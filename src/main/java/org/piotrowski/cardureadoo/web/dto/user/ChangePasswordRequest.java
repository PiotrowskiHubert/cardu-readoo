package org.piotrowski.cardureadoo.web.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank String username,
        @NotBlank @Size(min = 8, max = 200) String oldPassword,
        @NotBlank @Size(min = 8, max = 200) String newPassword
) { }
