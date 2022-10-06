package com.example.moviebot.response;

import com.example.moviebot.model.Movie;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MovieListResponse {
    private List<Movie> dtoList;
    private Long count;
}
