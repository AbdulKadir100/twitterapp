package com.kadir.abdul.Twitter_App.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ApiResponse<T> {
    private String responseMessage;
    private int responseCode;
    private T data;
    
    public ApiResponse(String responseMessage) {
        this.responseMessage = responseMessage;
    }
    public ApiResponse(String responseMessage, int responseCode) {
        this.responseMessage = responseMessage;
        this.responseCode = responseCode;
    }
    public ApiResponse(T data) {
        this.data = data;
    }
}
