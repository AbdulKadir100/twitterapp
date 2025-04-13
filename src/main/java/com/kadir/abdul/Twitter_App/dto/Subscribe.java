package com.kadir.abdul.Twitter_App.dto;

import com.kadir.abdul.Twitter_App.validator.NotNullOrMin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Subscribe {
    @NotNullOrMin
    private Long userId;
    @NotNullOrMin
    private Long subscriberID;

}
