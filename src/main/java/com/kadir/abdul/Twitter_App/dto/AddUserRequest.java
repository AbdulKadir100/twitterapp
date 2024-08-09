package com.kadir.abdul.Twitter_App.dto;

import com.kadir.abdul.Twitter_App.validator.NotNullOrEmpty;
import com.kadir.abdul.Twitter_App.validator.ValidRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddUserRequest {
    @NotNullOrEmpty
    private String uName;
    @ValidRole
    private String uRole;

}
