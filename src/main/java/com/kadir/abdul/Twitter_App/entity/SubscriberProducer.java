package com.kadir.abdul.Twitter_App.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subscriberproducer")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriberProducer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sp_ID")
    private Long spId;

    @Column(name = "subscriberId")
    private Long subscriberId;

    @Column(name = "producerId")
    private Long producerId;

    // Many SubscriberProducer entities to one User as a subscriber
    @ManyToOne
    @JoinColumn(name = "subscriberId", referencedColumnName = "uid", insertable = false, updatable = false)
    private User subscriber;

    // Many SubscriberProducer entities to one User as a producer
    @ManyToOne
    @JoinColumn(name = "producerId", referencedColumnName = "uid", insertable = false, updatable = false)
    private User producer;

}
