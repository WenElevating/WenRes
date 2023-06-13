package com.wmm.dto;

import com.wmm.entity.User;
import lombok.Data;

@Data
public class UserDto extends User {
    private String  Code;
}
