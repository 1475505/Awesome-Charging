package com.bupt.charger.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;

/**
 * @author ll （ created: 2023-05-26 19:03 )
 */
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String username;

    @NonNull
    private String password;

    private String car_id;

    public User() {}

    public User(String username, String password) {
        setUsername(username);
        setPassword(password);
    }
}