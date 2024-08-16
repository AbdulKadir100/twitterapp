package com.kadir.abdul.Twitter_App.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Builder
@Data
@Embeddable
@AllArgsConstructor
public class SubscriberProducerId implements Serializable{
    private Long subscriberId;
    private Long producerId;

}
