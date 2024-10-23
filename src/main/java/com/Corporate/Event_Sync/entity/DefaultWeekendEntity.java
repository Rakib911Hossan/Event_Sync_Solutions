//package com.Corporate.Event_Sync.entity;
//
//import com.fasterxml.jackson.annotation.JsonBackReference;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//@Table(name = "default_Weekend_Entity", schema = "public")
//public class DefaultWeekendEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
//    private Long id;
//
//    @OneToOne
//    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
//    @JsonBackReference
//    private User user;
//
//    @Column(name = "friday", nullable = true)
//    private Boolean friday = true;
//
//    @Column(name = "saturday", nullable = true)
//    private Boolean saturday = true;
//
//    @Column(name = "sunday", nullable = true)
//    private Boolean sunday = true;
//
//    @Column(name = "monday", nullable = true)
//    private Boolean monday = true;
//
//    @Column(name = "tuesday", nullable = true)
//    private Boolean tuesday = true;
//
//    @Column(name = "wednesday", nullable = true)
//    private Boolean wednesday = true;
//
//    @Column(name = "thursday", nullable = true)
//    private Boolean thursday = true;
//
//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//
//    @Column(name = "created_by", nullable = true)
//    private String createdBy;
//
//    @Column(name = "updated_by", nullable = true)
//    private String updatedBy;
//
//    @Column(name = "is_active", nullable = false)
//    private Boolean isActive;
//
//    @PrePersist
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//        updatedAt = LocalDateTime.now();
//        isActive = false; // default value
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = LocalDateTime.now();
//    }
//
//}
