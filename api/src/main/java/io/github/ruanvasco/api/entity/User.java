package io.github.ruanvasco.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @Column(length = 24)
    private String id;

    private String firstName;

    private String lastName;

    private String email;
}
