package com.example.moviebot.service;

import com.example.moviebot.util.HeaderUtil;
import com.example.moviebot.model.Movie;
import com.example.moviebot.response.MovieListResponse;
import com.example.moviebot.util.CurrentMessage;
import com.example.moviebot.util.MessageType;
import com.example.moviebot.util.UserDataService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class MovieService {
    private static long page = 0L;
    private static final Integer size = 6;

    private final UserDataService userDataService;
    long moviesCount;

    public MovieService(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    public CurrentMessage getAllMovies(Long chatId) {
        CurrentMessage currentMessage = new CurrentMessage();
        SendMessage sendMessage = new SendMessage();
        RestTemplate restTemplate = new RestTemplate();
        String token = userDataService.getUserInfo(chatId).getToken();
        HttpEntity<Void> request = new HttpEntity<>(HeaderUtil.headers(token));
        String url = String.format("http://localhost:8080/api/v1/movies/getAllMovies?page=%s&size=%s", page, size);
        ResponseEntity<MovieListResponse> response = restTemplate.exchange(url, HttpMethod.GET, request, MovieListResponse.class);
        if(response.getStatusCode().value() == 200){
            sendMessage.setText("All movies");
            sendMessage.setChatId(chatId);
            MovieListResponse result = response.getBody();
            List<Movie> movies = result.getDtoList();
            moviesCount = result.getCount();
            if(movies.size() < 1) {
                return null;
            }
            sendMessage.setReplyMarkup(generateMoviesButton(movies));
            currentMessage.setSendMessage(sendMessage);
            currentMessage.setMessageType(MessageType.SEND_MESSAGE);
            return currentMessage;
        }
        return null;
    }

    private InlineKeyboardMarkup generateMoviesButton(List<Movie> movieList){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        for(int i = 0; i< movieList.size(); i++){
            Movie movie = movieList.get(i);
            if(movieList.size() < 3){
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(movie.getName());
                inlineKeyboardButton.setCallbackData(String.format("get_movie/%d", movie.getId()));
                row.add(inlineKeyboardButton);
            }
            if(movieList.size() >= 3){
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(movie.getName());
                inlineKeyboardButton.setCallbackData(String.format("get_movie/%d", movie.getId()));
                row.add(inlineKeyboardButton);
                if(i == 2){
                    rowList.add(row);
                    row = new ArrayList<>();
                }
            }
        }
        if(!row.isEmpty()) {
            rowList.add(row);
        }
        boolean lastPage = page * size + movieList.size() != moviesCount;
        row = new ArrayList<>();
        if(page != 0){
            InlineKeyboardButton prev = new InlineKeyboardButton();
            prev.setText("<<");
            prev.setCallbackData("prev");
            row.add(prev);
        }
        if(page != 0 && lastPage){
            InlineKeyboardButton pageNumber = new InlineKeyboardButton();
            pageNumber.setText(String.valueOf(page));
            pageNumber.setCallbackData("pageNumber");
            row.add(pageNumber);
        }
        if(lastPage){
            InlineKeyboardButton next = new InlineKeyboardButton();
            next.setText(">>");
            next.setCallbackData("next");
            row.add(next);
        }
        rowList.add(row);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }


    public CurrentMessage prev(CallbackQuery callbackQuery) {
        page -= 1;
        return getAllMovies(callbackQuery.getMessage().getChatId());
    }

    public CurrentMessage next(CallbackQuery callbackQuery) {
        page += 1;
        return getAllMovies(callbackQuery.getMessage().getChatId());
    }

    public CurrentMessage getMovieById(Integer movieId, Long chatId) {
        Movie movie = getMovie(movieId, chatId);
        CurrentMessage currentMessage = new CurrentMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(getMarkUpForMovie(movieId, chatId));
        sendMessage.setParseMode("HTML");
        sendMessage.setText(String.format("<b>%s</b>\n%s\nCreated by: %s", movie.getName(), movie.getDescription(), movie.getUser().getName()));
        sendMessage.setChatId(chatId);
        currentMessage.setSendMessage(sendMessage);
        currentMessage.setMessageType(MessageType.SEND_MESSAGE);
        return currentMessage;
    }

    public Movie getMovie(Integer movieId, Long chatId){
        RestTemplate restTemplate = new RestTemplate();
        String token = userDataService.getUserInfo(chatId).getToken();
        HttpEntity<Void> request = new HttpEntity<>(HeaderUtil.headers(token));
        String url = "http://localhost:8080/api/v1/movies/" + movieId;
        ResponseEntity<Movie> response = restTemplate.exchange(url, HttpMethod.GET, request, Movie.class);
        return response.getBody();
    }

    private ReplyKeyboard getMarkUpForMovie(Integer movieId, Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton like = new InlineKeyboardButton();
        like.setText("Rate");
        like.setCallbackData(String.format("rate/%s/%s", chatId, movieId));
        InlineKeyboardButton comment = new InlineKeyboardButton();
        comment.setText("Comment");
        comment.setCallbackData(String.format("comment/%s/%s", chatId, movieId));
        row.add(like);
        row.add(comment);
        rowList.add(row);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }
}
