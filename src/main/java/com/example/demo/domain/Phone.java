package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity()
@EqualsAndHashCode(of = {"number"})
@ToString(exclude = "user")
public class Phone {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private int priority;
    private String number;
    @ManyToOne
    private User user;
}
