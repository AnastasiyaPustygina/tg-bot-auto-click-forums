package com.example.tgbotautoclickforums.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Forum {
    private final String name;
    private long timeDelaySeconds;
    private String url;
    private User user;
    private final IdElements idElements;

}
