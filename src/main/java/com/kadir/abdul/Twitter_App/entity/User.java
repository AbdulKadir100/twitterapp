package com.kadir.abdul.Twitter_App.entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;
    private String uName;
    private String uRole;

    @OneToMany(mappedBy = "subscriber", cascade = CascadeType.PERSIST)
    private Set<SubscriberProducer> subscriptions =  Collections.synchronizedSet(new HashSet<>());

    @OneToMany(mappedBy = "producer", cascade = CascadeType.PERSIST)
    private Set<SubscriberProducer> productions = Collections.synchronizedSet(new HashSet<>());

}
