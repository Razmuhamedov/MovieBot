package com.example.moviebot.service;

import com.example.moviebot.model.User;
import com.example.moviebot.util.CurrentMessage;
import com.example.moviebot.util.UserDataService;
import com.example.moviebot.util.UserState;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class BotService {
    private final MovieService movieService;
    private final GeneralService generalService;
    private final UserDataService userDataService;
    final
    AuthProcess authProcess;

    public BotService(MovieService movieService, GeneralService generalService, UserDataService userDataService, AuthProcess authProcess) {
        this.movieService = movieService;
        this.generalService = generalService;
        this.userDataService = userDataService;
        this.authProcess = authProcess;
    }

    public CurrentMessage handle(Update update) {
        if(update.hasMessage()){
           return handleMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
           return handleCallBack(update.getCallbackQuery());
        }
        return null;
    }

    private CurrentMessage handleMessage(Message message) {
        CurrentMessage currentMessage = new CurrentMessage();
        long chatId = message.getChatId();
        UserState state = userDataService.getUserState(chatId);
        if(state != null){
            if(state.equals(UserState.SIGN_UP_PROCESS)){
                return authProcess.inputName(message);
            }
            if(state.equals(UserState.INPUT_NAME)){
                return authProcess.inputName(message);
            }
            if(state.equals(UserState.INPUT_AGE)){
                return authProcess.inputAge(message);
            }
            if(state.equals(UserState.LOGIN_PROCESS) || state.equals(UserState.INPUT_EMAIL)){
                return authProcess.inputEmail(message);
            }
            if(state.equals(UserState.INPUT_PASSWORD)){
               return authProcess.inputPassword(message);
            }
        }
        if(message.hasText()){
            String inputMessage = message.getText();
            switch (inputMessage){
                case "/start":
                    currentMessage = generalService.handleStart(chatId);
                    break;
                case "/help":
                    currentMessage = generalService.handleHelp(chatId);
                    break;
                case "/movies":
                    currentMessage = movieService.getAllMovies(chatId);
                    break;
                case "/history":
//                 todo:   currentMessage = historyService.getHistory(chatId);
                    break;
                case "/comment":
                    break;
                case "/rate":
                    break;
            }
        }
        return currentMessage;
    }

    private CurrentMessage handleCallBack(CallbackQuery callbackQuery) {
        CurrentMessage currentMessage = new CurrentMessage();
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        switch (data){
            case "prev":
                currentMessage = movieService.prev(callbackQuery);
                break;
            case "next":
                currentMessage = movieService.next(callbackQuery);
                break;
        }
        User userInfo = userDataService.getUserInfo(chatId);
        if(data.startsWith("sign-up/")){
            return authProcess.signUpProcess(chatId);
        }
        if(data.startsWith("login/")){
            return authProcess.loginProcess(chatId);
        }
        if (data.startsWith("get_movie/")) {
            Integer movieId = Integer.valueOf(data.split("/")[1]);
            return movieService.getMovieById(movieId, chatId);
        }
        if(data.startsWith("like/")){
            if(userInfo == null){
               return authProcess.signUpProcess(chatId);
            } else if (!userInfo.getStatus()) {
               return authProcess.loginProcess(chatId);
            }

        }
        if(data.startsWith("comment/")){
            if(userInfo == null){
                return authProcess.signUpProcess(chatId);
            } else if (!userInfo.getStatus()) {
                return authProcess.loginProcess(chatId);
            }
        }
        return currentMessage;
    }


}
