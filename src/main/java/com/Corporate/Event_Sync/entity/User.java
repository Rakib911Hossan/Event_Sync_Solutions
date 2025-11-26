package com.Corporate.Event_Sync.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "user", schema = "public")
public class User extends GenericEntity<Integer>{

    private String name;
    private String phone;
    private String email;
    private String address;

    private String department;


    private String role; // Enum for roles like ADMIN, USER

    private String password;
    private String passToken;
    private String discountToken;
    private String userCategory;
    private Integer discountAmount;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders; // One user can have multiple orders
    private String userPic;
//    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<DefaultWeekDays> defaultWeekDays;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Set<LunchSchedule> lunchSchedules;
//
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Set<Feedback> feedbacks;


    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "office_id")
    private Integer officeId;

//    @OneToOne(mappedBy = "user",fetch = FetchType.LAZY)
//    @JsonManagedReference
//    private DefaultWeekendEntity defaultWeekend;

}
