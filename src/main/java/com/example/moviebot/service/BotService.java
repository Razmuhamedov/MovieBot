package com.example.moviebot.service;

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
    private final AuthProcess authProcess;
    private final RateService rateService;
    private final LibraryService libraryService;
    private final CommentService commentService;

    public BotService(MovieService movieService, GeneralService generalService, UserDataService userDataService, AuthProcess authProcess, RateService rateService, LibraryService libraryService, CommentService commentService) {
        this.movieService = movieService;
        this.generalService = generalService;
        this.userDataService = userDataService;
        this.authProcess = authProcess;
        this.rateService = rateService;
        this.libraryService = libraryService;
        this.commentService = commentService;
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
            if(state.equals(UserState.INPUT_RATE)){
                return rateService.setRate(message);
            }
            if(state.equals((UserState.INPUT_COMMENT))){
                return commentService.setComment(message);
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
                 currentMessage = libraryService.getHistory(chatId);
                    break;
                case "/comment":
                    currentMessage = commentService.getByUser(chatId);
                    break;
                case "/rate":
                    currentMessage = rateService.getByUser(chatId);
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
        if(data.startsWith("rate/")){
            Integer movieId = Integer.valueOf(data.split("/")[2]);
            return rateService.rateProcess(chatId, movieId);
        }
        if(data.startsWith("comment/")){
            Integer movieId = Integer.valueOf(data.split("/")[2]);
            return commentService.commentProcess(chatId, movieId);
        }
        if(data.startsWith("watchMovie/")){
            Integer movieId = Integer.valueOf(data.split("/")[2]);
            return libraryService.watchMovie(chatId, movieId);
        }
        return currentMessage;
    }

}
