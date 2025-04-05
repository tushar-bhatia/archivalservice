package com.process.archivalservice.model.request;


import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
public class PermissionRequest {

    private Integer id;

    @NotNull(message = "Please provide User ID for the user")
    @Positive
    private Integer userId;

    @NotNull(message = "Role can't be null.")
    @NotBlank(message = "Role can't be blank")
    @NotEmpty(message = "Role can't be empty")
    @Pattern(regexp = "ADMIN|admin|STUDENT|student|GRADES|grades|ATTENDANCE|attendance", message = "Role name should either be STUDENT or GRADES or ATTENDANCE or ADMIN")
    private String roleName;
}
