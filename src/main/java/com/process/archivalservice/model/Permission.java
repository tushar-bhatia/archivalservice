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
@Table(name = "permission", catalog="core")
public class Permission {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @jakarta.persistence.Column(name = "ID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @jakarta.persistence.Column(name = "ROLE_NAME")
    private String roleName;

    @JsonIgnore
    @jakarta.persistence.Column(name = "CREATED")
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
