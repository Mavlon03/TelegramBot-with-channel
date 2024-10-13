package uz.pdp.bot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static uz.pdp.bot.BotService.telegramBot;

public class BotController {
    static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public void start() {
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                executorService.execute(() -> {
                    try {
                        handleUpdates(update);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void handleUpdates(Update update) {
        Message message = update.message();

        if (message == null) {
            return;
        }

        Long chatId = message.chat().id();
        Long userId = message.from().id();
        String username = message.from().username();
        TgUser tgUser = BotService.saveAndNewMemeber2(chatId, username);
        System.out.println(username + " ⏭️ " + message.text() + " ni yozdi");


        if (update.myChatMember() != null) {
            handleUpdateMember(update.myChatMember());
        } else if (message.text() != null && message.text().equals("/start")) {
            BotService.acceptUserAndSendWelcome(tgUser);
        } else if (tgUser.getTgState().equals(TgState.SEND_CONTACT)) {
            ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
            SendMessage sendMessage = new SendMessage(tgUser.getId(), "");
            sendMessage.replyMarkup(replyKeyboardRemove);
            telegramBot.execute(sendMessage);
        } else if (message.contact() != null) {
            System.out.println(username + " ⏭️" + message.contact() + " ni yubordi");
            BotService.acceptContact(tgUser, message.contact());
        } else if (message.text() != null && message.text().contains("http")) {
            BotService.blockUser(chatId, userId, 1);
        }else if (update.inlineQuery() != null){
            InlineQuery inlineQuery = update.inlineQuery();
            String query = inlineQuery.query();
            BotService.inlineQuery(inlineQuery, query);
        }
    }

    private void handleUpdateMember(ChatMemberUpdated chatMemberUpdated) {
        if ((chatMemberUpdated.newChatMember().status().equals(ChatMember.Status.member))) {
            Long chatId = chatMemberUpdated.from().id();
            String userName = chatMemberUpdated.from().username();
            BotService.saveAndNewMemeber(chatId, userName);
        }
    }
}
