package com.reminder.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users", schema = "reminder")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Enumerated(EnumType.STRING)
    private Role role;
}
