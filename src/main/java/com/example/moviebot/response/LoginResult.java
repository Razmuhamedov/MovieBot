package com.example.moviebot.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResult {
    private String email;
    private String token;
}
