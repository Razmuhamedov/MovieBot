package com.example.moviebot.service;

import com.example.moviebot.model.User;
import com.example.moviebot.util.CurrentMessage;
import com.example.moviebot.util.MessageType;
import com.example.moviebot.util.UserDataService;
import com.example.moviebot.util.UserState;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class GeneralService {

    private final UserDataService userDataService;

    public GeneralService(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    public CurrentMessage handleStart(Long chatId){
        CurrentMessage currentMessage = new CurrentMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Please Sing-up or Login for watching movies");
        sendMessage.setReplyMarkup(getStartKeyboard());
        userDataService.updateUserState(chatId, UserState.INPUT_NAME);
        currentMessage.setSendMessage(sendMessage);
        currentMessage.setMessageType(MessageType.SEND_MESSAGE);
        return currentMessage;
    }
    public CurrentMessage handleHelp(Long chatId){
        CurrentMessage currentMessage = new CurrentMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("How can we help you!");
        currentMessage.setSendMessage(sendMessage);
        currentMessage.setMessageType(MessageType.SEND_MESSAGE);
        return currentMessage;
    }

    public ReplyKeyboard getStartKeyboard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton signUp = new InlineKeyboardButton();
        signUp.setText("Sign up");
        signUp.setCallbackData("sign-up/");
        InlineKeyboardButton login = new InlineKeyboardButton();
        row.add(signUp);
        login.setText("Login");
        login.setCallbackData("login/");
        row.add(login);
        rowList.add(row);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public ReplyKeyboard getHelpKeyboard(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();


        return null;
    }
}
