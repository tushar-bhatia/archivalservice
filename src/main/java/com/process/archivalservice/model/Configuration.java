package com.process.archivalservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Column;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;


@AllArgsConstructor
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
@Entity
@Table(name = "configuration", catalog="core")
public final class Configuration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "TABLE_NAME")
    private String tableName;

    @Column(name = "CONFIGURATION_TYPE")
    private String configurationType;

    @Column(name = "YEARS")
    private int years;

    @Column(name = "MONTHS")
    private int months;

    @Column(name = "WEEKS")
    private int weeks;

    @Column(name = "DAYS")
    private int days;

    @Column(name = "HOURS")
    private int hours;

    @Column(name = "MINUTES")
    private int minutes;

    @JsonIgnore
    @Column(name = "CREATED")
    private Timestamp created;

    @JsonIgnore
    @Column(name = "UPDATED")
    private Timestamp updated;

    @PrePersist
    protected void onCreate() {
        this.created = Timestamp.valueOf(LocalDateTime.now());
        this.updated = Timestamp.valueOf(LocalDateTime.now());
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated = Timestamp.valueOf(LocalDateTime.now());
    }
}
