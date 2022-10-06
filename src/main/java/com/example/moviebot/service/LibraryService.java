package com.example.moviebot.service;

import com.example.moviebot.model.User;
import com.example.moviebot.util.CurrentMessage;
import com.example.moviebot.util.UserDataService;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


@Service
public class LibraryService {
    private final UserDataService userDataService;

    public LibraryService(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    public CurrentMessage getHistory(long chatId) {
        Integer userId = userDataService.getUserInfo(chatId).getId();
        CurrentMessage currentMessage = new CurrentMessage();
        SendMessage sendMessage = new SendMessage();
        RestTemplate restTemplate = new RestTemplate();
return  null;
    }
}
