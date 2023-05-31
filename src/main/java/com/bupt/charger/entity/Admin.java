package com.bupt.charger.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;



/**
 * @author wyf （ created: 2023-05-29 11:00 )
 */
@Data
@Entity
@Table(name = "admins")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String adminName;

    @NonNull
    private String password;


    public Admin() {}

    public Admin(String adminName, String password) {
        setUsername(adminName);
        setPassword(password);
    }

    private void setUsername(String administratorName) {
        this.adminName=adminName;
    }


}
