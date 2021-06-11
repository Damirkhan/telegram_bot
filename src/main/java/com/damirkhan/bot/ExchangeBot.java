package com.damirkhan.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ExchangeBot extends TelegramLongPollingBot {

    private boolean firstCur;
    private boolean secondCur;

    private String toCur;
    private String fromCur;
    private String amount;

    public String getBotUsername() {
        return "ExchangeRateBot";
    }

    public String getBotToken() {
        return "1706195263:AAF9RW1X8Ph73glZJ4-IC6hSTkMUL3weDGY";
    }

    public void sendMsg(Update update, String text) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(String.valueOf(update.getMessage()
                        .getChatId())).text(text).build();
        try {
            setButtons(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();

        KeyboardRow keyboardRow1 = new KeyboardRow();

        keyboardRow1.add(new KeyboardButton("USD"));
        keyboardRow1.add(new KeyboardButton("EUR"));
        keyboardRow1.add(new KeyboardButton("RUB"));
        keyboardRow1.add(new KeyboardButton("KZT"));

        keyboardRowList.add(keyboardRow1);

        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    private boolean checkToEqual(String text) {
        if (text.equals("USD") || text.equals("EUR") || text.equals("RUB") || text.equals("KZT"))
            return true;
        return false;
    }

    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            if (message.getText().equals("/start")) {
                firstCur = false;
                secondCur = false;
                SendMessage sendMessage = SendMessage.builder()
                        .chatId(String.valueOf(update.getMessage()
                                .getChatId())).text("Let's start. \nPrint /help to know how to use this bot").build();
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message.getText().equals("/help")) {
                String rules = "Bot Usage Rules:\n" +
                        "1. Select the button with the base currency.\n" +
                        "2. Select the button with the currency you want to know the exchange rate for\n" +
                        "3. You can use /change comand to change Currency From\n" +
                        "4. Enter the amount \n" +
                        "5. Get the result";
                SendMessage sendMessage = SendMessage.builder()
                        .chatId(String.valueOf(message.getChatId())).text(rules).build();
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (checkToEqual(message.getText())) {

                if (!firstCur && !secondCur) {
                    fromCur = message.getText();
                    sendMsg(update, "Currency From: " + fromCur);
                    firstCur = true;
                } else if (firstCur && !secondCur) {
                    toCur = message.getText();
                    sendMsg(update, "Currency To: " + toCur + "\nInput amount:");
                    secondCur = true;
                } else if (firstCur && secondCur) {
                    toCur = message.getText();
                    sendMsg(update, "Currency To: " + toCur + "\nInput amount:");
                }

            } else if (message.getText().equals("/change")) {
                firstCur = false;
                secondCur = false;
                SendMessage sendMessage = SendMessage.builder()
                        .chatId(String.valueOf(message.getChatId()))
                        .text("Choose Currency From!").build();
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {
                if (firstCur && secondCur) {
                    Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
                    if (pattern.matcher(message.getText()).matches()) {
                        Rate rate = new Rate();
                        amount = message.getText();
                        try {
                            String result = rate.getRate(amount, fromCur, toCur);
                            SendMessage sendMessage = SendMessage.builder()
                                    .chatId(String.valueOf(message.getChatId())).text(result).build();
                            try {
                                execute(sendMessage);
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
    }
}
