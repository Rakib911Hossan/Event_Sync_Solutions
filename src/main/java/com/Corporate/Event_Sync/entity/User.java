package com.Corporate.Event_Sync.entity;

import com.Corporate.Event_Sync.utils.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String email;
    private String department;

    @Enumerated(EnumType.STRING)
    private Role role; // Enum for roles like ADMIN, USER

    private String password;
@JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders; // One user can have multiple orders


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
