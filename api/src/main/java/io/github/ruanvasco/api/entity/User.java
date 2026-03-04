package io.github.ruanvasco.api.entity;

import jakarta.persistence.*;

@Entity
public class User {

    @Id
    @Column(length = 24)
    private String id;

    private String firstName;

    private String lastName;

    private String email;
}
