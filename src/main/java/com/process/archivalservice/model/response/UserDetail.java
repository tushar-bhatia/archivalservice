package com.process.archivalservice.model.response;

import com.process.archivalservice.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetail {
    int userId;
    String userName;
    Set<String> roles;
}
