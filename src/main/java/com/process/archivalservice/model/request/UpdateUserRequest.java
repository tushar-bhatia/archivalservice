package com.process.archivalservice.model.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
public class UpdateUserRequest {

    @NotNull(message = "User name can't be null")
    @NotEmpty(message = "User name can't be empty")
    @NotBlank(message = "User name can't be blank")
    String username;

    @NotNull(message = "Password can't be null")
    @NotEmpty(message = "Password can't be empty")
    @NotBlank(message = "Password can't be blank")
    String currentPassword;

    @NotNull(message = "New password can't be null")
    @NotEmpty(message = "New password can't be empty")
    @NotBlank(message = "New password can't be blank")
    String newPassword;

}
