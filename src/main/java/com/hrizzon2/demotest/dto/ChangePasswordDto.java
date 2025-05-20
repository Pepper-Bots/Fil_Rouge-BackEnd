package com.hrizzon2.demotest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDto {

    private String email;

    private String newPassword;

    public CharSequence getNewPassword() {
        return newPassword;
    }
}
