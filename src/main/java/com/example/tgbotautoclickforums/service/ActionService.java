package com.example.tgbotautoclickforums.service;

import com.example.tgbotautoclickforums.configuration.BotConfig;
import com.example.tgbotautoclickforums.domain.Forum;
import com.example.tgbotautoclickforums.exception.ForumNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActionService extends TelegramLongPollingBot{
    private final BotConfig botConfig;

    private final ForumService forumService;

    @Value("${bot.token}")
    private String botToken;

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId().toString());
            message.setReplyToMessageId(update.getMessage().getMessageId());
            try {
                if(update.hasCallbackQuery()) {
                    String[] data = update.getCallbackQuery().getData().split("/");
                    switch (data[0]) {
                        case "getForum" -> {
                            showForumInfo(data, message);
                            break;
                        }
                        case "update" -> {
                            updateValue(data, message);
                            break;
                        }
                        case "getForums" -> {
                            showForums(message);
                            break;
                        }
                        case "startExecution" ->{
                            RestTemplate restTemplate = new RestTemplate();
                            restTemplate.postForLocation(URI.create("http://192.168.1.56:8081/start"), Void.class);
                        }

                    }
                }
                else{
                    switch (update.getMessage().getText()) {
                        case "/start" -> {
                            forumService.updateForums();
                            message.setText("Рады вас видеть! \n Благодаря этому боту вы можете изменять данные форумов и следить за выполнением скрипта");
                            showForums(message);
                        }
                        case "/" -> {

                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    private InlineKeyboardButton createUpdateButton(String updateElement, String name){
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Change " + updateElement);
        button.setCallbackData("update/" + name + "/" + updateElement);
        return button;
    }
    private void showForumInfo(String[] data, SendMessage message) throws TelegramApiException {
        String name = data[1];
        Forum forum = forumService.getForums().stream().filter(f -> f.getName().equals(name))
                .findFirst().orElseThrow(() -> new ForumNotFoundException(name));
        message.setText("Редактирование данных форума " + forum);
        InlineKeyboardMarkup forumInlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(new ArrayList<>(List.of(createUpdateButton("login", name))));
        if(name.equals("WWHClub")){
            rowList.add(new ArrayList<>(List.of(createUpdateButton("secretWord", name))));
        }
        rowList.add(new ArrayList<>(List.of(createUpdateButton("password", name))));
        rowList.add(new ArrayList<>(List.of(createUpdateButton("url", name))));
        rowList.add(new ArrayList<>(List.of(createUpdateButton("timeDelaySeconds", name))));
        forumInlineKeyboardMarkup.setKeyboard(rowList);
        message.setReplyMarkup(forumInlineKeyboardMarkup);
        execute(message);
    }
    private void updateValue(String[] data, SendMessage message) throws TelegramApiException {
        String name = data[1];
        Forum forum = forumService.getForums().stream().filter(f -> f.getName().equals(name))
                .findFirst().orElseThrow(() -> new ForumNotFoundException(name));
        Object forumValue = new Object();
        switch (data[2]){
            case "login" -> {
                forumValue = forum.getUser().getLogin();
                break;
            }
            case "password" -> {
                forumValue = forum.getUser().getPassword();
                break;
            }
            case "secretWord" ->{
                forumValue = forum.getUser().getSecretWord();
                break;
            }
            case "url" ->{
                forumValue = forum.getUrl();
                break;
            }
            case "timeDelaySeconds" ->{
                forumValue = forum.getTimeDelaySeconds();
            }
        }
        message.setText("Сейчас " + data[2] + " для форума " + name + ": " + forumValue + "\n Введите новое значение");
        execute(message);
    }
    private void showForums(SendMessage message) throws TelegramApiException {
        List<Forum> forums = forumService.getForums();
        InlineKeyboardMarkup forumInlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtons = new ArrayList<>();
        forums.forEach(f -> {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(f.getName());
            button.setCallbackData("getForum/" + f.getName());
            keyboardButtons.add(button);
        });
        keyboardButtons.forEach(b -> {
            rowList.get(keyboardButtons.indexOf(b) / 2).add(b);
        });
        forumInlineKeyboardMarkup.setKeyboard(rowList);
        message.setReplyMarkup(forumInlineKeyboardMarkup);
        execute(message);
    }
}
