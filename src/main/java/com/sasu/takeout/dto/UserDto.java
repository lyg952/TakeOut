package com.sasu.takeout.dto;

import com.sasu.takeout.entity.User;
import lombok.Data;

@Data
public class UserDto extends User {
    private String code;
}
