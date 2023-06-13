package com.example.tgbotautoclickforums.exception;

public class ForumNotFoundException extends RuntimeException{
    public ForumNotFoundException(String name) {
        super("forum with name" + name + " was not found");
    }
}
