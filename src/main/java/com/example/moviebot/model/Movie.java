package com.example.moviebot.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class Movie {
    private Integer id;
    private String name;
    private String description;
    private User user;
    private Double rate;
}
