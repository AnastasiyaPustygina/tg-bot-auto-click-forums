package com.example.tgbotautoclickforums.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Message {
    private final String forumUrl;
    private String text;

    private Status status;
}
