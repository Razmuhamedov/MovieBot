package com.example.moviebot.response;

import com.example.moviebot.model.Movie;
import com.example.moviebot.model.Rate;
import com.example.moviebot.model.User;
import com.example.moviebot.service.MovieService;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RateResponse {
    private Integer score;
    private User user;
    private Movie movie;
}
