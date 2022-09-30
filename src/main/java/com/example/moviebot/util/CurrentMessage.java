package com.example.moviebot.util;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

@Getter
@Setter
public class CurrentMessage {
    private MessageType messageType;
    private SendMessage sendMessage;
    private SendPhoto sendPhoto;
}
