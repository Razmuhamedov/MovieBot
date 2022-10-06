package com.example.moviebot.service;

import com.example.moviebot.model.SignUp;
import com.example.moviebot.model.User;
import com.example.moviebot.util.CurrentMessage;
import com.example.moviebot.util.MessageType;
import com.example.moviebot.util.UserDataService;
import com.example.moviebot.util.UserState;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class AuthProcess {
    private final AuthService authService;
    private final UserDataService userDataService;
    private final UserService userService;

    public AuthProcess(AuthService authService, UserDataService userDataService, UserService userService) {
        this.authService = authService;
        this.userDataService = userDataService;
        this.userService = userService;
    }

    public CurrentMessage loginProcess(Long chatId) {
        userDataService.updateUserState(chatId, UserState.LOGIN_PROCESS);
        CurrentMessage currentMessage = new CurrentMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode("HTML");
        sendMessage.setText("<b>LOGIN</b>\nPlease, input your email");
        sendMessage.setChatId(chatId);
        currentMessage.setMessageType(MessageType.SEND_MESSAGE);
        currentMessage.setSendMessage(sendMessage);
        return currentMessage;
    }

    public CurrentMessage signUpProcess(Long chatId) {
        userDataService.updateUserState(chatId, UserState.INPUT_NAME);
        CurrentMessage currentMessage = new CurrentMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode("HTML");
        sendMessage.setText("<b>Registration</b>\nPlease, input your name");
        sendMessage.setChatId(chatId);
        currentMessage.setMessageType(MessageType.SEND_MESSAGE);
        currentMessage.setSendMessage(sendMessage);
        return currentMessage;
    }

    public Boolean getStatus(Long chatId){
        User user = userDataService.getUserInfo(chatId);
        return user != null && user.getStatus();

    }

    public CurrentMessage inputEmail(Message message) {
        CurrentMessage currentMessage = new CurrentMessage();
        Long chatId = message.getChatId();
        String email = message.getText();
        User user = userDataService.getUserInfo(chatId);
        if(user == null){
            user = new User();
        }
        user.setEmail(email);
        user.setChatId(chatId);
        userDataService.insertUserInfo(user, chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Please, input your password");
        userDataService.updateUserState(chatId, UserState.INPUT_PASSWORD);
        currentMessage.setSendMessage(sendMessage);
        currentMessage.setMessageType(MessageType.SEND_MESSAGE);
        return currentMessage;
    }

    public CurrentMessage inputPassword(Message message) {
        Long chatId = message.getChatId();
        CurrentMessage currentMessage = new CurrentMessage();
        SendMessage sendMessage = new SendMessage();
        String password = message.getText();
        User user = userDataService.getUserInfo(chatId);
        user.setPassword(password);
        user.setStatus(true);
        User u = userService.getUserByEmail(user.getEmail());
        String text;
        if(u == null){
            text = authService.signUp(user);
            sendMessage.setText(text);
        }else {
            user = authService.login(user);
            userDataService.insertUserInfo(user, chatId);
            sendMessage.setText("You can watch movies!");
        }
        sendMessage.setChatId(chatId);
        userDataService.updateUserState(chatId, UserState.INPUT_DATA);
        currentMessage.setSendMessage(sendMessage);
        currentMessage.setMessageType(MessageType.SEND_MESSAGE);
        return currentMessage;
    }

    public CurrentMessage inputName(Message message) {
        CurrentMessage currentMessage = new CurrentMessage();
        SendMessage sendMessage = new SendMessage();
        Long chatId = (message.getChatId());
        String name = message.getText();
        User user = userDataService.getUserInfo(chatId);
        if(user == null){
            user = new User();
        }
        user.setName(name);
        userDataService.insertUserInfo(user, chatId);
        sendMessage.setText("Please, input your age");
        sendMessage.setChatId(chatId);
        userDataService.updateUserState(chatId, UserState.INPUT_AGE);
        currentMessage.setMessageType(MessageType.SEND_MESSAGE);
        currentMessage.setSendMessage(sendMessage);
        return currentMessage;
    }

    public CurrentMessage inputAge(Message message) {
        CurrentMessage currentMessage = new CurrentMessage();
        SendMessage sendMessage = new SendMessage();
        Long chatId = message.getChatId();
        Integer age = Integer.valueOf(message.getText());
        User user = userDataService.getUserInfo(chatId);
        user.setAge(age);
        userDataService.insertUserInfo(user, chatId);
        sendMessage.setText("Please, input your email");
        sendMessage.setChatId(chatId);
        userDataService.updateUserState(chatId, UserState.INPUT_EMAIL);
        currentMessage.setSendMessage(sendMessage);
        currentMessage.setMessageType(MessageType.SEND_MESSAGE);
        return currentMessage;
    }
}
