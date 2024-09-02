package com.kadir.abdul.Twitter_App.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublishMesssageRequest {
    @NotNull(message = "User ID cannot be null")
    private Long userId;
    @NotEmpty(message = "Message cannot be null")
    private String message;

}
