package com.kadir.abdul.Twitter_App.dto;

import com.kadir.abdul.Twitter_App.validator.NotNullOrMin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Subscribe {
    @NotNullOrMin
    private Long userId;
    @NotNullOrMin
    private Long subscriberID;

}
