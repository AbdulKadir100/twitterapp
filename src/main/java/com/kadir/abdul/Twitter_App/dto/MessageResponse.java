package com.kadir.abdul.Twitter_App.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponse {
    private String contents;
    private Long mid;
    private UserResponse postedBy;

}
