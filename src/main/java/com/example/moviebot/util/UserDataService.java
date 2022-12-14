package com.example.moviebot.util;

import com.example.moviebot.model.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserDataService {
    private Map<Long, User> userMap = new HashMap<>();
    private Map<Long, UserState> userStateMap = new HashMap<>();

    public void insertUserInfo(User user, Long chatId){
        userMap.put(chatId, user);
    }



    public User getUserInfo(Long chatId){
        return userMap.get(chatId);
    }

    public void updateUserState(Long chatId, UserState state){
        userStateMap.put(chatId, state);
    }

    public UserState getUserState(Long chatId){
        return userStateMap.get(chatId);
    }

}
