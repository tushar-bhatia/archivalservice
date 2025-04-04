package com.process.archivalservice.model;

import lombok.*;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@Builder
public final class Configuration {

    protected Configuration() {}
    private Integer id;
    private String tableName;
    private String configurationType;
    private int years;
    private int months;
    private int weeks;
    private int days;
    private int hours;
    private int minutes;
    private Timestamp created;
    private Timestamp updated;
}
