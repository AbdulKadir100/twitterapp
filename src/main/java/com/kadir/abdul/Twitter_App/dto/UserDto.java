package com.kadir.abdul.Twitter_App.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserDto {
    private Long uid;
    private String uName;
    private String uRole;
   // private Set<SubscriberProducer> subscriptions;
   // private Set<SubscriberProducer> productions;

}
