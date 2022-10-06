package com.example.moviebot.service;

import com.example.moviebot.model.Movie;
import com.example.moviebot.response.RateResponse;
import com.example.moviebot.util.HeaderUtil;
import com.example.moviebot.model.Rate;
import com.example.moviebot.model.User;
import com.example.moviebot.util.CurrentMessage;
import com.example.moviebot.util.MessageType;
import com.example.moviebot.util.UserDataService;
import com.example.moviebot.util.UserState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Service
public class RateService {

    private final UserDataService userDataService;
    private final UserService userService;
    private Integer movieId;
    @Value("${movieService.url}")
    private String Url;

    public RateService(UserDataService userDataService, UserService userService) {
        this.userDataService = userDataService;
        this.userService = userService;
    }

    public CurrentMessage rateProcess(Long chatId, Integer movieId) {
        this.movieId = movieId;
        userDataService.updateUserState(chatId, UserState.INPUT_RATE);
        CurrentMessage currentMessage = new CurrentMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Please, input digit from 0 to 10");
        sendMessage.setChatId(chatId);
        currentMessage.setMessageType(MessageType.SEND_MESSAGE);
        currentMessage.setSendMessage(sendMessage);
        return currentMessage;
    }

    public CurrentMessage setRate(Message message) {
        CurrentMessage currentMessage = new CurrentMessage();
        SendMessage sendMessage = new SendMessage();
        Long chatId = message.getChatId();
        Integer rateValue = Integer.valueOf(message.getText());
        User user = userDataService.getUserInfo(chatId);
        Integer userId = userService.getUserByEmail(user.getEmail()).getId();
        Rate rate = new Rate();
        rate.setMovieId(movieId);
        rate.setUserId(userId);
        rate.setScore(rateValue);
        HttpEntity<Rate> entity = new HttpEntity<>(rate, HeaderUtil.headers(user.getToken()));
        String url = Url + "rate/createRate/";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        userDataService.updateUserState(chatId, UserState.INPUT_DATA);
        sendMessage.setChatId(chatId);
        sendMessage.setText(response.getBody());
        currentMessage.setSendMessage(sendMessage);
        currentMessage.setMessageType(MessageType.SEND_MESSAGE);
        return currentMessage;
    }

    public CurrentMessage getByUser(long chatId) {
        User user = userDataService.getUserInfo(chatId);
        String url = String.format("%srate/getMyRates/", Url);
        HttpEntity<Void> entity = new HttpEntity<>(HeaderUtil.headers(user.getToken()));
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<RateResponse[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, RateResponse[].class);
        SendMessage sendMessage = new SendMessage();
        if(response.getStatusCodeValue() != 200){
            sendMessage.setText(response.getBody().toString());
        }else {
        List<RateResponse> rateList = List.of(response.getBody());
        sendMessage.setText("<b>Your rates: </b>" + rateList.size());
        sendMessage.setParseMode("HTML");
        sendMessage.setChatId(chatId);
        for (RateResponse rate:rateList) {
            Movie movie = rate.getMovie();
            sendMessage.setText(sendMessage.getText() + "\n\n"
                                + "<b>1) Movie</b> - " + movie.getName()+ "\n"
                                + "<b>Rate</b> - " + rate.getScore());
        }}
        CurrentMessage currentMessage = new CurrentMessage();
        currentMessage.setMessageType(MessageType.SEND_MESSAGE);
        currentMessage.setSendMessage(sendMessage);
        return currentMessage;
    }
}
