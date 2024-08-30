package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Cascade;
import org.springframework.data.repository.cdi.Eager;

import java.util.HashSet;
import java.util.Set;
@Data
@Entity(name = "Userx")
@EqualsAndHashCode(of = {"name"})
@ToString()
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;
    @Column(name = "name")
    public String name;
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public Set<Phone> phone = new HashSet<>();
}
