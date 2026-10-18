package com.durgesh.project.airBnbApp.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name ="hotel")
public class Hotel {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long hotelId;

    @Column(nullable=false)
    private String hotelName;

    private String city;

    @Column(columnDefinition = "TEXT[]")
    private String[] photos;

    @Column(columnDefinition = "TEXT[]")
    private String[] amenities;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Embedded
    private HotelContactInfo hotelContactInfo;

    @Column(nullable = false)
    private Boolean active;

}
