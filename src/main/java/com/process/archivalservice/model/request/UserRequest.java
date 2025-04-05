package com.process.archivalservice.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
public class UserRequest {

    @Positive
    private Integer id;

    @NotNull(message = "User name can't be null")
    @NotEmpty(message = "User name can't be empty")
    @NotBlank(message = "User name can't be blank")
    String username;

    @NotNull(message = "Password can't be null")
    @NotEmpty(message = "Password can't be empty")
    @NotBlank(message = "Password can't be blank")
    String password;
}
