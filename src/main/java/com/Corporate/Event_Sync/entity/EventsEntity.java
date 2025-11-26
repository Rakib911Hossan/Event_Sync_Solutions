//package com.Corporate.Event_Sync.entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//
//import java.io.Serializable;
//import java.time.LocalDate;
//
//@Entity
//@Data
//@Table(name = "event", schema = "public")
//public class EventsEntity implements Serializable {
//
//    private static final long serialVersionUID = 1L;
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
//    private Integer id;
//
//    @Column(name = "name", nullable = false)
//    private String name;
//
//    @Column(name = "start_date", nullable = false)
//    private LocalDate startDate;
//
//    @Column(name = "end_date", nullable = false)
//    private LocalDate endDate;
//
//    @Column(name = "status", nullable = false)
//    private String status;
//
//    @Column(name = "code", nullable = false)
//    private String code;
//
//}
