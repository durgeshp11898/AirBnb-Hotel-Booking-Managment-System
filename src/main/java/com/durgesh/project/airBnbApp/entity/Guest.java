package com.durgesh.project.airBnbApp.entity;

import com.durgesh.project.airBnbApp.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guestId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String guestName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Integer age;
}

