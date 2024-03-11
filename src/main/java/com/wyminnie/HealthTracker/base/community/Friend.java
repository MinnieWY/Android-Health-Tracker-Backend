package com.wyminnie.healthtracker.base.community;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "friends")
public class Friend {
    @Id
    private Long id;
    private Long user1;
    private Long user2;
    private String status;
}
