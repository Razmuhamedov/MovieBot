package com.example.moviebot.util;

import org.springframework.http.HttpHeaders;

public class HeaderUtil {

public static HttpHeaders headers(String token){
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + token);
    return headers;
}

}
