package com.process.archivalservice.model;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Student {
    protected Student(){}
    private Integer id;
    private String name;
    private LocalDate dob;
    private Character gender;
    private LocalDateTime created;
    private LocalDateTime updated;
}
