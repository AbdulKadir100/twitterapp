package com.kadir.abdul.Twitter_App.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublishMesssageRequest {
    @NotNull(message = "User ID cannot be null")
    public Long userId;
    @NotEmpty(message = "Message cannot be null")
    public String message;

}
