package com.process.archivalservice.model.request;

import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
public class ConfigRequest {

    private Integer id;

    @NotNull(message = "Table name can't be null")
    @NotEmpty(message = "Table name can't be empty")
    @NotBlank(message = "Table name can't be blank")
    @NotNull(message = "Please provide a valid table name")
    private String tableName;

    @NotNull(message = "Configuration type can't be null")
    @NotEmpty(message = "Configuration type can't be empty")
    @NotBlank(message = "Configuration type can't be blank")
    @Pattern(regexp = "ARCHIVAL|DELETION", message = "configuration type can either be ARCHIVAL or DELETION")
    private String configurationType;

    @PositiveOrZero(message = "Year should be a either 0 or a positive number")
    private Integer years;

    @PositiveOrZero(message = "Months should be a either 0 or a positive number")
    private Integer months;

    @PositiveOrZero(message = "Weeks should be a either 0 or a positive number")
    private Integer weeks;

    @PositiveOrZero(message = "Days should be a either 0 or a positive number")
    private Integer days;

    @PositiveOrZero(message = "Hours should be a either 0 or a positive number")
    private Integer hours;

    @PositiveOrZero(message = "Minutes should be a either 0 or a positive number")
    private Integer minutes;
}
