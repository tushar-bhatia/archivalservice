package com.process.archivalservice.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
public class PermissionRequest {

    private Integer id;

    @NotNull(message = "Username can't be null.")
    @NotBlank(message = "Username can't be blank")
    @NotEmpty(message = "Username can't be empty")
    private String userName;

    @NotNull(message = "Role can't be null.")
    @NotBlank(message = "Role can't be blank")
    @NotEmpty(message = "Role can't be empty")
    @Pattern(regexp = "ADMIN|STUDENT|GRADES|ATTENDANCE", message = "Role name should either be STUDENT or GRADES or ATTENDANCE or ADMIN")
    private String roleName;
}
